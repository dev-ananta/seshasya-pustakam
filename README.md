# Seshasya — landing site

A static website for the Seshasya app and the book *Seshasya Pustakam*.

- **index.html** — landing page: hero → interactive app demo → download → book mockup → footer
- **read.html** — reads the book in the browser (no download), using a bundled copy of PDF.js
- No build step, no framework, no tracking. Everything runs from these files.

````markdown
index.html          landing page
read.html           in-browser book reader
css/style.css       landing page styles
css/reader.css      reader styles
js/main.js          nav, scroll reveal, and the clickable phone demo
js/reader.js        PDF.js reader (lazy rendering, page jump, zoom, contents)
assets/book/        Seshasya_Pustakam.pdf
assets/img/         cover, emblem, favicon, social preview image
downloads/          put Seshasya.apk here (see downloads/README.txt)
vendor/pdfjs/       PDF.js (Apache-2.0, see vendor/pdfjs/LICENSE)
````

## Preview on your computer

The reader loads the PDF with JavaScript, which browsers block on `file://`.
So serve the folder instead of double-clicking `index.html`:

```bash
cd seshasya-site
python3 -m http.server 8000
```

Then open <http://localhost:8000>. (The landing page and demo also work when opened
directly; only the reader needs the server.)

## Put it online free with GitHub Pages

1. Create a repository (or use your app's repository) and upload the **contents** of this
   folder to the root of the `main` branch, or to a `docs/` folder.
2. On GitHub: **Settings → Pages → Build and deployment → Deploy from a branch**,
   choose `main` and `/ (root)` (or `/docs`), and save.
3. After a minute your site is at `https://<username>.github.io/<repo>/`.

Netlify, Cloudflare Pages and Vercel also work: drag the folder in, no settings needed.

## Set the download link

The **Download for Android** button points to `downloads/Seshasya.apk`. Either:

- copy your built APK into `downloads/` with that exact name, or
- edit `id="apk-link"` in `index.html` to point at your GitHub release, e.g.
  `https://github.com/<you>/<repo>/releases/latest/download/Seshasya.apk`

## Update the book

Replace `assets/book/Seshasya_Pustakam.pdf` with a new version (same file name).
If the page count changes, update the "129 pages" figure in the book section of `index.html`.
To link to a specific page of the reader, use `read.html#page=42`.

## Notes

- The demo on the home page is a simulation. Nothing typed in it is saved or sent anywhere.
- Fonts (Cormorant Garamond and Inter) load from Google Fonts. Offline, the page falls back to system fonts.
- Motion is reduced automatically for visitors who have "reduce motion" turned on.
