/* Seshasya landing page: nav, scroll reveal, and an interactive demo of the app. */
(function () {
  "use strict";

  /* ---------------- page chrome ---------------- */
  var nav = document.getElementById("nav");
  function onScroll() { nav.classList.toggle("scrolled", window.scrollY > 24); }
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  if ("IntersectionObserver" in window) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) {
        if (e.isIntersecting) { e.target.classList.add("in"); io.unobserve(e.target); }
      });
    }, { threshold: 0.15 });
    document.querySelectorAll(".reveal").forEach(function (el) { io.observe(el); });
  } else {
    document.querySelectorAll(".reveal").forEach(function (el) { el.classList.add("in"); });
  }

  var clock = document.getElementById("clock");
  function tickClock() {
    var d = new Date();
    clock.textContent = d.getHours() % 12 === 0 ? "12" + ":" + pad(d.getMinutes()) : (d.getHours() % 12) + ":" + pad(d.getMinutes());
  }
  function pad(n) { return (n < 10 ? "0" : "") + n; }
  tickClock(); setInterval(tickClock, 20000);

  /* ---------------- demo content ---------------- */
  var routine = [
    { time: "First light", title: "Wake with the dawn", tasks: [
      { id: "earth", t: "Touch the earth", h: "Thank Dhara, who bears you." },
      { id: "lamp", t: "Light a lamp", h: "For Anala, the eternal flame within." } ] },
    { time: "Morning", title: "Orientation", tasks: [
      { id: "creed", t: "Recite the Creed", h: "Om Anantaya Namah…", go: "library" },
      { id: "mantra", t: "Chant one mala (108)", h: "Om Anantaya Namah.", go: "japa" },
      { id: "doctrine", t: "Choose today’s doctrine", h: "See Today’s light above." } ] },
    { time: "Sitting", title: "Stillness", tasks: [
      { id: "sit", t: "Sit 10–20 minutes", h: "In: Tat tvam asi. Out: Avi yahan hai.", go: "sit" } ] },
    { time: "The day", title: "Application", tasks: [
      { id: "records", t: "Read the Records", h: "Find first principles before a decision.", go: "ledger" },
      { id: "speech", t: "True, kind, helpful speech", h: "Ask the four questions first." } ] },
    { time: "Evening", title: "Reflection", tasks: [
      { id: "mirror", t: "Mirror review", h: "What helped? What harmed?" },
      { id: "hara", t: "Give one burden to Hara", h: "Name it, then set it down." } ] },
    { time: "Night", title: "Rest", tasks: [
      { id: "ledger", t: "Write the Ananta ledger", h: "Effort · Growth · Remainder.", go: "ledger" },
      { id: "metta", t: "Loving-kindness", h: "To yourself, and to all beings." } ] }
  ];
  var allIds = [];
  routine.forEach(function (b) { b.tasks.forEach(function (t) { allIds.push(t.id); }); });

  var doctrines = [
    ["Perseverance", "Ganesha", "Return to a worthy task after every setback."],
    ["Patience & Kindness", "Krishna", "Pause before reacting; let others explain themselves."],
    ["Forgiveness", "Vishnara", "Release vengeance; keep memory, boundaries and safety."],
    ["Integrity & Autonomy", "Shiva", "Change your mind for evidence, never for approval."],
    ["Dignity", "Indra", "Refuse to be humiliated, and never humiliate anyone."],
    ["Potential", "Ananta Sesha", "Learn deliberately; treat ability as something to grow."],
    ["Chivalry & Reciprocation", "Parvati", "Help without pressure; answer care with care."],
    ["Openness", "Varuna", "Ask what evidence would change your mind."],
    ["Rightful Pride & Pity", "Surya", "Know your worth without lowering anyone else’s."],
    ["Assistance & Humility", "Chandra", "Offer useful help; admit what you do not know."],
    ["Hunger", "Garuda", "Set meaningful goals; rest is part of achievement."],
    ["Hope", "Lakshmi", "Name the next possible step, even in darkness."],
    ["Temporal Insignificance", "Yama", "Remember time is limited; choose what matters."],
    ["Negation", "Alexander von Corvus", "Feel the first arrow; do not add a second."],
    ["Acceptance", "Horace of the Scorched Sun", "Admire someone better; learn; keep improving."],
    ["Enlightenment & Dedication", "The Buddha", "Study, reflect, test, and keep going."]
  ];
  var devas = [
    ["Indra","Dignity"],["Aryaman","Chivalry & Reciprocation"],["Bhaga","Hope"],["Tvashtar","Potential"],
    ["Varuna","Openness & Forgiveness"],["Mitra","Patience & Kindness"],["Pushan","Assistance & Humility"],
    ["Vivasvan","Rightful Pride"],["Savitr","Enlightenment & Dedication"],["Amsha","Acceptance"],
    ["Dhata","Perseverance & Equilibrium"],["Vishnu","Forgiveness & Boundless Ascension"],
    ["Ajaikapada","Integrity & Autonomy"],["Ahirbudhnya","The Drifting Leaf"],["Virupaksha","Openness"],
    ["Raivata","Hunger"],["Hara","Negation"],["Bahurupa","Infinite Possibilities"],["Tryambaka","Temporal Insignificance"],
    ["Savitra","Hope"],["Jayanta","Perseverance"],["Pinaki","Dedication"],["Aparajita","Eternal Effort = Reward"],
    ["Dhara","Patience & Kindness"],["Dhruva","Integrity & Dedication"],["Soma","Assistance & Humility"],
    ["Ahas","Perseverance"],["Anila","Hunger"],["Anala","The Eternal Flame"],["Pratyusha","Hope"],
    ["Prabhasa","Enlightenment"],["Nasatya","Integrity"],["Dasra","Assistance"]
  ];
  var tenets = [
    ["I · Infinite Possibilities", "What remains after everything passes is always full. No future is closed."],
    ["II · Limitless Growth", "Measure yourself against who you were, not against others."],
    ["III · Boundless Ascension", "Rise without ceiling — by bearing others, never by climbing over them."],
    ["IV · Eternal Effort = Reward", "No sincere effort is lost. Charaiveti — keep moving."]
  ];
  var week = ["Vivasvan and the Adityas: greet the sun.", "Soma and the Rudras: healing and release.",
    "Courage and Perseverance: face something difficult.", "Study: read from any sacred text.",
    "Teachers and elders: thank a teacher.", "Lakshmi and the Vasus: generosity and nature.",
    "Ananta Sesha: rest and reflection."];

  /* ---------------- demo state ---------------- */
  var S = {
    tab: "today", sitSeg: 0, done: { earth: true, lamp: true },
    minutes: 1, endAt: 0, pausedLeft: 0, sitFinished: false, sitToday: 0,
    japa: 0, mantra: 0, ledger: [], lib: null, open: {}
  };
  var mantras = ["Om Anantaya Namah", "Om Namo Narayanaya", "Tat tvam asi · Avi yahan hai"];
  var screen = document.getElementById("screen");
  var timerId = null;

  var today = new Date();
  var start = new Date(today.getFullYear(), 0, 0);
  var dayOfYear = Math.floor((today - start) / 86400000);
  var docToday = doctrines[(dayOfYear - 1) % doctrines.length];
  var devaToday = devas[(dayOfYear - 1) % devas.length];

  function esc(s) { return String(s).replace(/[&<>"]/g, function (c) { return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]; }); }
  function buzz(ms) { try { if (navigator.vibrate) navigator.vibrate(ms); } catch (e) {} }
  function count() { return allIds.filter(function (id) { return S.done[id]; }).length; }

  /* ---------------- screens ---------------- */
  function viewToday() {
    var c = count(), total = allIds.length, circ = 2 * Math.PI * 30;
    var off = circ * (1 - c / total);
    var dateStr = today.toLocaleDateString(undefined, { weekday: "long", day: "numeric", month: "long" });
    var h = '<div class="fade-in">' +
      '<div class="a-label">Avi yahan hai · it is here, now</div>' +
      '<div class="a-h">' + esc(dateStr) + '</div>' +
      '<div class="a-sub">Tat tvam asi — That you are.</div>' +
      '<div class="a-card prog"><div class="ring"><svg viewBox="0 0 70 70"><circle class="trk" cx="35" cy="35" r="30"/>' +
      '<circle class="val" cx="35" cy="35" r="30" stroke-dasharray="' + circ + '" stroke-dashoffset="' + off + '"/></svg>' +
      '<div class="lbl"><div>' + c + "/" + total + '<small>today</small></div></div></div>' +
      '<div><div><span class="fire">▲</span> <b>4 days of practice</b></div>' +
      '<div class="a-muted">Sitting today: ' + S.sitToday + ' min</div>' +
      '<div class="a-muted">' + (S.ledger.length ? "Ananta ledger written" : "Ananta ledger not yet written") + '</div>' +
      (total - c > 0 ? '<div style="color:var(--sage);font-size:12.5px">' + (total - c) + (total - c === 1 ? " step remains." : " steps remain.") + '</div>' : "") +
      '</div></div>' +
      '<div class="a-card gold"><div class="a-label">Today’s doctrine</div><div class="a-title">' + esc(docToday[0]) + '</div>' +
      '<div class="a-muted">' + esc(docToday[2]) + '</div><div class="a-hr"></div>' +
      '<div class="a-label">Deva of the day</div><div class="a-title">' + esc(devaToday[0]) + ' — ' + esc(devaToday[1]) + '</div>' +
      '<div class="a-hr"></div><div class="a-label">The Seshan week</div><div class="a-muted">' + esc(week[today.getDay()]) + '</div></div>';
    routine.forEach(function (b) {
      var bd = b.tasks.filter(function (t) { return S.done[t.id]; }).length;
      h += '<div class="a-card"><div class="blk-h"><div><div class="a-label sage">' + esc(b.time) + '</div><b>' + esc(b.title) + '</b></div>' +
        '<span class="a-muted">' + bd + "/" + b.tasks.length + '</span></div>';
      b.tasks.forEach(function (t) {
        h += '<div style="display:flex;align-items:center"><button class="task' + (S.done[t.id] ? " done" : "") + '" data-task="' + t.id + '" aria-pressed="' + (!!S.done[t.id]) + '">' +
          '<span class="chk"></span><span class="tt"><b>' + esc(t.t) + '</b><span>' + esc(t.h) + '</span></span></button>' +
          (t.go ? '<button class="go" data-go="' + t.go + '" aria-label="Open ' + esc(t.t) + '">›</button>' : "") + '</div>';
      });
      h += "</div>";
    });
    if (c === total) {
      h += '<div class="a-card green fade-in"><div style="font-size:26px;color:var(--gold)">❀</div>' +
        '<div class="a-title">Today’s practice is complete</div><div class="a-muted" style="color:#cbe5b6">No sincere effort is lost. Charaiveti.</div></div>';
    }
    return h + "</div>";
  }

  function viewSit() {
    var h = '<div class="fade-in"><div class="a-label">Stillness</div><div class="a-h">Sit</div>' +
      '<div class="seg"><button data-seg="0" class="' + (S.sitSeg === 0 ? "on" : "") + '">Meditation</button>' +
      '<button data-seg="1" class="' + (S.sitSeg === 1 ? "on" : "") + '">Japa</button></div>';
    if (S.sitSeg === 0) {
      var idle = !S.endAt && !S.pausedLeft;
      h += '<div class="chips">' + [1, 5, 10, 20].map(function (m) {
        return '<button class="chip' + (S.minutes === m ? " on" : "") + '" data-min="' + m + '"' + (idle ? "" : " disabled") + '>' + m + ' min</button>';
      }).join("") + '</div>' +
        '<div class="breath"><svg viewBox="0 0 230 230"><circle cx="115" cy="115" r="108" fill="none" stroke="#22312A" stroke-width="4"/>' +
        '<circle id="arc" cx="115" cy="115" r="108" fill="none" stroke="#D9BF7C" stroke-width="4" stroke-linecap="round" stroke-dasharray="' + (2 * Math.PI * 108) + '" stroke-dashoffset="' + (2 * Math.PI * 108) + '"/></svg>' +
        '<div class="orb" id="orb"></div><div class="txt"><div class="time" id="time"></div><div class="phase" id="phase"></div></div></div>';
      if (idle) {
        h += '<button class="a-btn" data-act="begin">▶&nbsp; Begin sitting</button>';
      } else {
        h += '<div class="a-row"><button class="a-btn ghost" data-act="end">End</button><button class="a-btn" data-act="pause">' + (S.endAt ? "Pause" : "Resume") + '</button></div>';
      }
      h += '<p class="a-muted" style="text-align:center;margin:8px 0 0">Demo tip: choose 1 min to see the full cycle.</p>';
      if (S.sitFinished) {
        h += '<div class="a-card green fade-in"><div class="a-title">Sitting complete</div><div class="a-muted" style="color:#cbe5b6">' + S.minutes + ' min added. The flame is tended.</div></div>';
      }
    } else {
      var inMala = S.japa % 108, circ = 2 * Math.PI * 104;
      h += '<div class="chips">' + mantras.map(function (m, i) {
        return '<button class="chip' + (S.mantra === i ? " on" : "") + '" data-mantra="' + i + '">' + esc(m) + '</button>';
      }).join("") + '</div>' +
        '<div style="text-align:center;color:var(--gold);font-family:var(--serif);font-size:19px;margin-top:10px">' + esc(mantras[S.mantra]) + '</div>' +
        '<button class="japa" data-act="bead" aria-label="Count one repetition"><svg viewBox="0 0 230 230"><circle cx="115" cy="115" r="104" fill="none" stroke="#2b3c33" stroke-width="6"/>' +
        '<circle cx="115" cy="115" r="104" fill="none" stroke="#9CC283" stroke-width="6" stroke-linecap="round" stroke-dasharray="' + circ + '" stroke-dashoffset="' + (circ * (1 - inMala / 108)) + '"/></svg>' +
        '<span><span class="n">' + inMala + '</span><br><span class="a-muted">of 108</span><br><span class="a-label">Mala ' + Math.floor(S.japa / 108) + '</span></span></button>' +
        '<p class="a-muted" style="text-align:center;margin:0 0 8px">Tap the circle for each repetition.</p>' +
        '<button class="a-btn ghost" data-act="jreset">Reset count</button>';
    }
    return h + "</div>";
  }

  function viewLedger() {
    var h = '<div class="fade-in"><div class="a-label">Effort · Growth · Remainder</div><div class="a-h">Ledger</div>' +
      '<p class="a-sub" style="margin:4px 0 0">Three short lines each night. Drop by drop, the water-pot fills.</p>' +
      '<label class="f-lbl" for="le">Effort</label><textarea class="field" id="le" rows="2" placeholder="One thing I strove at today"></textarea>' +
      '<label class="f-lbl" for="lg">Growth</label><textarea class="field" id="lg" rows="2" placeholder="How I differ from a month ago"></textarea>' +
      '<label class="f-lbl" for="lr">Remainder</label><textarea class="field" id="lr" rows="2" placeholder="One possibility open tomorrow"></textarea>' +
      '<button class="a-btn" data-act="lsave" style="margin-top:12px">Add today’s drop</button>' +
      '<p class="a-muted" style="margin:8px 0 0">Your ledger holds ' + S.ledger.length + (S.ledger.length === 1 ? " drop." : " drops.") + '</p>';
    S.ledger.forEach(function (e) {
      h += '<div class="entry fade-in"><div class="a-label sage">Tonight</div>' +
        (e[0] ? "<div><b>Effort:</b> " + esc(e[0]) + "</div>" : "") +
        (e[1] ? "<div><b>Growth:</b> " + esc(e[1]) + "</div>" : "") +
        (e[2] ? "<div><b>Remainder:</b> " + esc(e[2]) + "</div>" : "") + "</div>";
    });
    return h + "</div>";
  }

  function viewLibrary() {
    var h = '<div class="fade-in">';
    if (!S.lib) {
      h += '<div class="a-label">Seshasya Pustakam</div><div class="a-h">Library</div>';
      [["creed", "The Central Creed", "Ananta Sesha and the four tenets"],
       ["doctrines", "The Sixteen Doctrines", "Branches of the Creed"],
       ["devas", "The Thirty-Three Devas", "Adityas, Rudras, Vasus, Ashvins"],
       ["sayings", "The Two Great Sayings", "Tat Tvam Asi · Avi Yahan Hai"]].forEach(function (s) {
        h += '<button class="lib-item" data-lib="' + s[0] + '"><span><b>' + s[1] + '</b><span>' + s[2] + '</span></span><span>›</span></button>';
      });
      return h + "</div>";
    }
    h += '<button class="back" data-act="libback">← Library</button>';
    if (S.lib === "creed") {
      h += '<div class="a-card gold" style="text-align:center"><div class="a-title">Ananta Sesha</div><div class="a-sub">the Endless Remainder</div>' +
        '<p style="margin:8px 0 0;font-size:13px">Possibility is infinite, growth is limitless, ascension is boundless, and eternal effort is its own reward.</p></div>';
      tenets.forEach(function (t) { h += '<div class="a-card"><div style="color:var(--gold);font-weight:600">' + esc(t[0]) + '</div><div class="a-muted">' + esc(t[1]) + '</div></div>'; });
    } else if (S.lib === "doctrines") {
      doctrines.forEach(function (d, i) {
        h += '<div class="exp' + (S.open[i] ? " open" : "") + '"><button data-exp="' + i + '"><span class="num">' + (i + 1) + '</span><span><b>' + esc(d[0]) + '</b><br><span class="a-muted">' + esc(d[1]) + '</span></span></button>' +
          '<div class="body">' + esc(d[2]) + '</div></div>';
      });
    } else if (S.lib === "devas") {
      devas.forEach(function (d, i) {
        var fam = i < 12 ? "Adityas" : i < 23 ? "Rudras" : i < 31 ? "Vasus" : "Ashvins";
        h += '<div class="lib-item" style="cursor:default"><span><b>' + esc(d[0]) + '</b><span>' + fam + " · " + esc(d[1]) + '</span></span></div>';
      });
    } else {
      h += '<div class="a-card"><div style="color:var(--gold);font-weight:600">Tat Tvam Asi</div><div class="a-muted">“That you are.” The Endless is your own deepest self.</div></div>' +
        '<div class="a-card"><div style="color:var(--gold);font-weight:600">Avi Yahan Hai</div><div class="a-muted">“It is here, now.” The Endless is present in this place and this breath.</div></div>';
    }
    return h + "</div>";
  }

  /* ---------------- render + timer ---------------- */
  function render() {
    var v = S.tab === "today" ? viewToday() : S.tab === "sit" ? viewSit() : S.tab === "ledger" ? viewLedger() : viewLibrary();
    screen.innerHTML = v;
    document.querySelectorAll(".tabbar button").forEach(function (b) { b.classList.toggle("on", b.dataset.tab === S.tab); });
    document.querySelectorAll(".feature").forEach(function (b) {
      var on = b.dataset.tab === S.tab;
      b.classList.toggle("active", on); b.setAttribute("aria-pressed", String(on));
    });
    if (S.tab === "sit" && S.sitSeg === 0) paintTimer();
  }

  function remaining() {
    var total = S.minutes * 60000;
    if (S.endAt) return Math.max(0, S.endAt - Date.now());
    if (S.pausedLeft) return S.pausedLeft;
    return total;
  }
  function paintTimer() {
    var t = document.getElementById("time"); if (!t) return;
    var total = S.minutes * 60000, rem = remaining(), secs = Math.ceil(rem / 1000);
    t.textContent = Math.floor(secs / 60) + ":" + pad(secs % 60);
    var arc = document.getElementById("arc"), circ = 2 * Math.PI * 108;
    arc.setAttribute("stroke-dashoffset", circ * (rem / total));
    var orb = document.getElementById("orb"), phase = document.getElementById("phase");
    if (S.endAt) {
      var cyc = Date.now() % 10000, inhale = cyc < 4000;
      var sc = inhale ? 0.62 + 0.38 * (cyc / 4000) : 1 - 0.38 * ((cyc - 4000) / 6000);
      orb.style.transform = "scale(" + sc + ")";
      phase.textContent = inhale ? "Breathe in\nTat tvam asi" : "Breathe out\nAvi yahan hai";
    } else {
      orb.style.transform = "scale(.8)";
      phase.textContent = S.pausedLeft ? "Paused" : "Ready";
    }
  }
  function loop() {
    if (!S.endAt) { timerId = null; return; }
    if (Date.now() >= S.endAt) {
      S.endAt = 0; S.sitFinished = true; S.sitToday += S.minutes; S.done.sit = true;
      buzz([200, 100, 200, 100, 350]); render(); timerId = null; return;
    }
    if (S.tab === "sit" && S.sitSeg === 0) paintTimer();
    timerId = requestAnimationFrame(loop);
  }
  function startLoop() { if (!timerId) timerId = requestAnimationFrame(loop); }

  /* ---------------- events ---------------- */
  function setTab(tab) { S.tab = tab; if (tab !== "library") S.lib = null; render(); screen.scrollTop = 0; }

  document.querySelectorAll(".tabbar button, .feature").forEach(function (b) {
    b.addEventListener("click", function () { setTab(b.dataset.tab); });
  });

  screen.addEventListener("click", function (e) {
    var el = e.target.closest("button"); if (!el) return;
    var d = el.dataset;
    if (d.task) { S.done[d.task] = !S.done[d.task]; buzz(12); var y = screen.scrollTop; render(); screen.scrollTop = y; return; }
    if (d.go) {
      if (d.go === "japa") { S.sitSeg = 1; setTab("sit"); }
      else if (d.go === "sit") { S.sitSeg = 0; setTab("sit"); }
      else setTab(d.go);
      return;
    }
    if (d.seg) { S.sitSeg = +d.seg; render(); return; }
    if (d.min) { S.minutes = +d.min; S.sitFinished = false; render(); return; }
    if (d.mantra) { S.mantra = +d.mantra; render(); return; }
    if (d.lib) { S.lib = d.lib; render(); screen.scrollTop = 0; return; }
    if (d.exp) { S.open[d.exp] = !S.open[d.exp]; var y2 = screen.scrollTop; render(); screen.scrollTop = y2; return; }
    switch (d.act) {
      case "begin": S.sitFinished = false; S.endAt = Date.now() + S.minutes * 60000; render(); startLoop(); break;
      case "pause":
        if (S.endAt) { S.pausedLeft = Math.max(1, S.endAt - Date.now()); S.endAt = 0; }
        else { S.endAt = Date.now() + S.pausedLeft; S.pausedLeft = 0; startLoop(); }
        render(); break;
      case "end": S.endAt = 0; S.pausedLeft = 0; render(); break;
      case "bead":
        S.japa++;
        if (S.japa % 108 === 0) { buzz([200, 100, 300]); S.done.mantra = true; } else buzz(8);
        var n = screen.scrollTop; render(); screen.scrollTop = n; break;
      case "jreset": S.japa = 0; render(); break;
      case "lsave":
        var v = [document.getElementById("le").value.trim(), document.getElementById("lg").value.trim(), document.getElementById("lr").value.trim()];
        if (v[0] || v[1] || v[2]) { S.ledger.unshift(v); S.done.ledger = true; render(); }
        break;
      case "libback": S.lib = null; render(); break;
    }
  });

  render();
})();
