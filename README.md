# 📚 NovelGrab

**Paste one chapter link. Get the whole story, saved on your phone and ready to read offline.**

NovelGrab is a small Android app that follows the "Next chapter" links on web novel and manga/manhwa/manhua sites and saves every page for offline reading. No accounts, no ads, no clutter: one box, one button.

---

## ✨ Features

- **One-tap flow** – paste the first chapter URL, press **Start download**, done.
- **Works on many sites** – finds the next page from `rel="next"` tags, "Next" buttons and arrows (›, »), chapter numbers in the URL (`chapter-18`, `_1.html`, `Chapter-1-Title/13452180/`), and links hidden in page scripts. It falls back to guessing the next URL.
- **Smart stopping** – ends when there is no next link, never loops back to old pages, ignores "Prev" and disabled buttons, and stays on the same website.
- **Manga-ready images** – handles lazy-loaded images, images inside `<noscript>`, and image lists hidden in JavaScript. Images are saved *inside* each file, so chapters work with no internet.
- **Live progress** – progress bar, chapter list and a log while it runs.
- **Chapter limit** – download everything, or just the next 10, 50, 100…
- **Fast but polite** – chapters are found one by one, while images are processed in parallel (1, 5, 10 or 15 at once) with an adjustable delay between pages.
- **Read inside the app** – tap a finished chapter to open it, with Prev / Next buttons.
- **Clean reader mode** – removes site scripts and (optionally) site styling for distraction-free reading.
- **Your files, your phone** – everything is saved as normal `.html` files you can open anywhere.

## 📥 Install (for readers)

1. Open this repository's **Releases** page on your phone and download the latest `app-debug.apk`.
2. Tap the file. If Android asks, allow **Install unknown apps** for your browser or Files app (one time).
3. Open **NovelGrab**.

> Android may show a Play Protect warning for apps installed outside the Play Store. That is normal for sideloaded apps.

## 🚀 How to use

1. Open the first chapter of a story in your browser and copy its link.
2. Paste it into NovelGrab.
3. *(Optional)* Enter how many chapters you want. Leave empty for all.
4. Tap **Start download** and keep the app open until it finishes.
5. Tap any chapter in the list to read it, or open **Documents/NovelGrab/** in your Files app.

Saved files look like this:

```
Documents/NovelGrab/<site>_<date-time>/
  001_Chapter_1.html
  002_Chapter_2.html
  ...
  index.html        <- a simple table of contents
```

### Advanced options

| Option | What it does |
|---|---|
| Chapters processed at once | How many pages are saved in parallel (default 5) |
| Delay between chapters | Pause between requests, default 0.5 s. Raise it if a site blocks you |
| Images | Save inside the file (offline), keep links only, or skip images |
| Page style | Clean reader, or the original site style |

## 🛠 Build the APK yourself (no installs needed)

You only need a free GitHub account.

1. Create a new repository and upload **all files from this project**, including the hidden `.github` folder. If `.github` is missing, use **Add file → Create new file**, name it `.github/workflows/build.yml` and paste the file contents.
2. Open the **Actions** tab. The **Build APK** workflow runs automatically after each upload, or press **Run workflow**.
3. After about 5–8 minutes, download `app-debug.apk` from the **Releases** page (or from the workflow run's *Artifacts*).

Building locally instead: install Node 20 and JDK 17, then run
`npm install`, `npx cap add android`, `npx cap sync android` and `cd android && ./gradlew assembleDebug`.
The workflow also adds a few Android settings (storage permission for old phones, plain-HTTP support), so copy the `sed` lines from `.github/workflows/build.yml` if you build by hand.

## 🧩 How it works

- A Capacitor app (HTML + JavaScript in a native Android shell).
- Pages are fetched with Capacitor's native HTTP, which is not limited by browser CORS rules.
- The next link is chosen by scoring candidates: `rel=next` > the same URL pattern with chapter number +1 > "Next" text or class names > links in scripts > a guessed URL.
- Each page is cleaned (scripts removed), its images are downloaded and embedded as base64, and the result is saved with the Capacitor Filesystem plugin.

Project layout:

```
www/index.html                 the whole app (UI + crawler)
capacitor.config.json          app name and settings
package.json                   dependencies
.github/workflows/build.yml    builds the APK in the cloud
```

## ❓ Troubleshooting

| Problem | Try this |
|---|---|
| Stops after the first chapter | The site may use a non-standard next button. Open an issue with the link |
| Images missing or `FAILED` | Lower "Chapters processed at once" to 1–3, raise the delay to 1–2 s, or set Images to "Keep links only" |
| Chapter looks empty | The site builds pages with JavaScript only, which this app cannot run |
| "HTTP 403" | The site blocks automated downloads (e.g. Cloudflare protection) |
| Download pauses | Keep the app open and the screen on. Android pauses background apps |
| Cannot find the files | Look in **Documents → NovelGrab** in your Files app |
| APK will not install | Allow "Install unknown apps" for the app you opened it from |

## ⚠️ Limitations

- Debug-signed APK: fine for personal use, not for the Play Store.
- Very large manga chapters can be slow and use a lot of storage on low-memory phones.
- Some sites cannot be downloaded at all because of login walls, anti-bot checks or JavaScript-only reading.

## ⚖️ Responsible use

NovelGrab is for **personal offline reading**. Respect each website's terms of use and the rights of authors and translators. Support creators when you can, don't redistribute downloaded content, and keep the delay setting polite so you don't overload small sites.

## 📄 License

Add the license of your choice (for example MIT) as a `LICENSE` file.
# novelgrab
