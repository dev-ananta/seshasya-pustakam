/* Seshasya Pustakam reader — renders the book in the page with PDF.js (no download). */
import * as pdfjsLib from "../vendor/pdfjs/pdf.min.mjs";

pdfjsLib.GlobalWorkerOptions.workerSrc = new URL("../vendor/pdfjs/pdf.worker.min.mjs", import.meta.url).href;

const BOOK_URL = "assets/book/Seshasya_Pustakam.pdf";
const $ = (id) => document.getElementById(id);
const viewer = $("viewer"), pagesEl = $("pages"), numInput = $("page-num");
const zoomBtn = $("zoom-fit");

let pdf = null;
let baseSize = null;      // size of page 1 at scale 1
let mode = "fit";          // "fit" or a numeric scale
let scale = 1;
let current = 1;
const slots = [];          // { el, canvas, rendered, task, scale }
let toc = [];              // [{ title, page, depth }]

const params = new URLSearchParams(location.search);
const startPage = parseInt(params.get("page") || location.hash.replace("#page=", ""), 10) || 1;

/* ---------- pages ---------- */
function buildSlots() {
  const frag = document.createDocumentFragment();
  for (let i = 1; i <= pdf.numPages; i++) {
    const el = document.createElement("div");
    el.className = "page";
    el.dataset.page = i;
    el.setAttribute("role", "img");
    el.setAttribute("aria-label", "Page " + i);
    const n = document.createElement("span");
    n.className = "num"; n.textContent = i;
    el.appendChild(n);
    frag.appendChild(el);
    slots[i] = { el, canvas: null, rendered: 0, task: null };
  }
  pagesEl.appendChild(frag);
}

function computeScale() {
  if (mode === "fit") {
    const avail = Math.min(viewer.clientWidth - 20, 900);
    scale = Math.max(0.4, avail / baseSize.w);
    zoomBtn.textContent = "Fit";
  } else {
    scale = mode;
    zoomBtn.textContent = Math.round(scale * 100) + "%";
  }
}

function layout() {
  const w = Math.floor(baseSize.w * scale), h = Math.floor(baseSize.h * scale);
  for (let i = 1; i <= pdf.numPages; i++) {
    const s = slots[i];
    s.el.style.width = w + "px";
    s.el.style.height = h + "px";
    s.el.style.containIntrinsicSize = w + "px " + h + "px";
  }
}

async function render(i) {
  const s = slots[i];
  if (!s || s.rendered === scale || s.task) return;
  const page = await pdf.getPage(i);
  const dpr = Math.min(window.devicePixelRatio || 1, 2);
  const vp = page.getViewport({ scale });
  const canvas = document.createElement("canvas");
  canvas.width = Math.floor(vp.width * dpr);
  canvas.height = Math.floor(vp.height * dpr);
  const want = scale;
  s.task = page.render({ canvas, viewport: vp, transform: dpr !== 1 ? [dpr, 0, 0, dpr, 0, 0] : null });
  try {
    await s.task.promise;
    if (want !== scale) return;           // zoom changed mid-render
    if (s.canvas) s.canvas.remove();
    s.el.prepend(canvas);
    s.canvas = canvas;
    s.rendered = want;
  } catch (e) {
    if (e && e.name !== "RenderingCancelledException") console.warn(e);
  } finally {
    s.task = null;
  }
}

function release(i) {
  const s = slots[i];
  if (s.task) { s.task.cancel(); s.task = null; }
  if (s.canvas) { s.canvas.width = s.canvas.height = 0; s.canvas.remove(); s.canvas = null; }
  s.rendered = 0;
}

/* Render pages near the viewport; free canvases far from it to keep memory low on phones. */
let io;
function observe() {
  if (io) io.disconnect();
  io = new IntersectionObserver((entries) => {
    for (const e of entries) {
      const i = +e.target.dataset.page;
      if (e.isIntersecting) render(i);
    }
  }, { root: viewer, rootMargin: "150% 0px" });
  for (let i = 1; i <= pdf.numPages; i++) io.observe(slots[i].el);
}

function sweep() {
  for (let i = 1; i <= pdf.numPages; i++) {
    if (slots[i].canvas && Math.abs(i - current) > 8) release(i);
  }
}

/* ---------- navigation ---------- */
function goTo(n, smooth = true) {
  n = Math.min(Math.max(1, n | 0), pdf.numPages);
  const el = slots[n].el;
  viewer.scrollTo({ top: el.offsetTop - 12, behavior: smooth && !reduced() ? "smooth" : "auto" });
  setCurrent(n);
}
function reduced() { return matchMedia("(prefers-reduced-motion: reduce)").matches; }

function setCurrent(n) {
  if (n === current && numInput.value == n) return;
  current = n;
  if (document.activeElement !== numInput) numInput.value = n;
  $("prev").disabled = n <= 1;
  $("next").disabled = n >= pdf.numPages;
  history.replaceState(null, "", "#page=" + n);
  markToc();
}

let ticking = false;
viewer.addEventListener("scroll", () => {
  if (ticking || !pdf) return;
  ticking = true;
  requestAnimationFrame(() => {
    ticking = false;
    const mid = viewer.scrollTop + viewer.clientHeight * 0.35;
    const h = slots[1].el.offsetHeight + 14;
    const n = Math.min(pdf.numPages, Math.max(1, Math.floor((mid - 18) / h) + 1));
    setCurrent(n);
    clearTimeout(sweep.t); sweep.t = setTimeout(sweep, 400);
  });
}, { passive: true });

$("prev").onclick = () => goTo(current - 1);
$("next").onclick = () => goTo(current + 1);
numInput.addEventListener("change", () => goTo(parseInt(numInput.value, 10) || 1));
numInput.addEventListener("keydown", (e) => { if (e.key === "Enter") { numInput.blur(); goTo(parseInt(numInput.value, 10) || 1); } });

document.addEventListener("keydown", (e) => {
  if (!pdf || e.target === numInput) return;
  if (e.key === "ArrowRight" || e.key === "PageDown" && e.altKey) { goTo(current + 1); e.preventDefault(); }
  else if (e.key === "ArrowLeft") { goTo(current - 1); e.preventDefault(); }
  else if (e.key === "Home" && e.ctrlKey) goTo(1);
  else if (e.key === "End" && e.ctrlKey) goTo(pdf.numPages);
  else if ((e.ctrlKey || e.metaKey) && (e.key === "=" || e.key === "+")) { zoom(1.2); e.preventDefault(); }
  else if ((e.ctrlKey || e.metaKey) && e.key === "-") { zoom(1 / 1.2); e.preventDefault(); }
  else if (e.key === "Escape") closeToc();
});

/* ---------- zoom ---------- */
function applyZoom() {
  const keep = current;
  for (let i = 1; i <= pdf.numPages; i++) release(i);
  computeScale();
  layout();
  goTo(keep, false);
  observe();
}
function zoom(f) {
  if (!pdf) return;
  const next = Math.min(4, Math.max(0.4, scale * f));
  mode = Math.round(next * 100) / 100;
  applyZoom();
}
$("zoom-in").onclick = () => zoom(1.2);
$("zoom-out").onclick = () => zoom(1 / 1.2);
zoomBtn.onclick = () => { if (!pdf) return; mode = "fit"; applyZoom(); };

let rz;
addEventListener("resize", () => {
  if (!pdf || mode !== "fit") return;
  clearTimeout(rz);
  rz = setTimeout(applyZoom, 200);
});

/* ---------- contents ---------- */
async function loadOutline() {
  const list = $("toc-list");
  try {
    const outline = await pdf.getOutline();
    if (!outline || !outline.length) { list.innerHTML = '<p class="muted">This book has no contents list.</p>'; return; }
    const walk = async (items, depth) => {
      for (const it of items) {
        let page = null;
        try {
          let dest = it.dest;
          if (typeof dest === "string") dest = await pdf.getDestination(dest);
          if (Array.isArray(dest)) page = (await pdf.getPageIndex(dest[0])) + 1;
        } catch (e) { /* skip unresolved */ }
        if (page) toc.push({ title: it.title, page, depth });
        if (it.items && it.items.length && depth < 1) await walk(it.items, depth + 1);
      }
    };
    await walk(outline, 0);
    list.innerHTML = "";
    for (const t of toc) {
      const a = document.createElement("a");
      a.href = "#page=" + t.page;
      a.className = t.depth ? "sub" : "";
      a.innerHTML = "<div></div><span>" + t.page + "</span>";
      a.firstChild.textContent = t.title;
      a.onclick = (e) => { e.preventDefault(); closeToc(); goTo(t.page); };
      list.appendChild(a);
    }
    markToc();
  } catch (e) {
    list.innerHTML = '<p class="muted">Contents unavailable.</p>';
  }
}
function markToc() {
  const links = $("toc-list").querySelectorAll("a");
  let idx = -1;
  toc.forEach((t, i) => { if (t.page <= current) idx = i; });
  links.forEach((a, i) => a.classList.toggle("current", i === idx));
}

const tocEl = $("toc"), scrim = $("scrim"), tocBtn = $("toc-btn");
function openToc() {
  tocEl.hidden = false; scrim.hidden = false;
  requestAnimationFrame(() => tocEl.classList.add("open"));
  tocBtn.setAttribute("aria-expanded", "true");
  const cur = tocEl.querySelector("a.current") || tocEl.querySelector("a");
  if (cur) { cur.scrollIntoView({ block: "center" }); cur.focus({ preventScroll: true }); }
}
function closeToc() {
  if (tocEl.hidden) return;
  tocEl.classList.remove("open"); scrim.hidden = true;
  tocBtn.setAttribute("aria-expanded", "false");
  setTimeout(() => { tocEl.hidden = true; }, reduced() ? 0 : 250);
}
tocBtn.onclick = () => (tocEl.hidden ? openToc() : closeToc());
$("toc-close").onclick = closeToc;
scrim.onclick = closeToc;

/* ---------- load ---------- */
const task = pdfjsLib.getDocument({
  url: BOOK_URL,
  wasmUrl: new URL("../vendor/pdfjs/wasm/", import.meta.url).href,
  standardFontDataUrl: new URL("../vendor/pdfjs/standard_fonts/", import.meta.url).href,
});
task.onProgress = ({ loaded, total }) => {
  if (total) $("progress").style.width = Math.min(100, (loaded / total) * 100) + "%";
};

async function init() {
try {
  pdf = await task.promise;
  const first = await pdf.getPage(1);
  const vp = first.getViewport({ scale: 1 });
  baseSize = { w: vp.width, h: vp.height };
  $("page-count").textContent = pdf.numPages;
  numInput.max = pdf.numPages;
  buildSlots();
  computeScale();
  layout();
  $("loading").remove();
  goTo(Math.min(Math.max(1, startPage), pdf.numPages), false);
  observe();
  loadOutline();
} catch (err) {
  console.error(err);
  const l = $("loading");
  l.classList.add("error");
  const local = location.protocol === "file:";
  l.querySelector("p").innerHTML = local
    ? "The reader can’t open the book from a local file.<br>Serve this folder (see README) or <a href=\"" + BOOK_URL + "\">open the PDF directly</a>."
    : "The book couldn’t be opened. <a href=\"" + BOOK_URL + "\">Open the PDF directly</a>.";
  l.querySelector(".bar-track").remove();
}
}
init();
