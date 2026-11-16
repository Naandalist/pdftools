"""Renders slides.html into the Play Store images in ../play with headless Chrome.

Usage: python3 store-screenshots/generator/build.py
Swap a phone by replacing its PNG in phones/ (transparent background, same frame) and running this again.
The overview strip is built from the screenshots, so they are rendered first.
"""
import functools, http.server, pathlib, subprocess, threading

HERE = pathlib.Path(__file__).resolve().parent
OUT = HERE.parent / "play"
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
SLIDES = ["01-all-tools", "02-compress", "03-watermark", "04-files", "05-private", "06-overview"]


class Quiet(http.server.SimpleHTTPRequestHandler):
    def log_message(self, *args):
        pass


server = http.server.ThreadingHTTPServer(("127.0.0.1", 0), functools.partial(Quiet, directory=str(HERE.parent)))
threading.Thread(target=server.serve_forever, daemon=True).start()
base = f"http://127.0.0.1:{server.server_address[1]}/{HERE.name}/slides.html"


def shoot(name, query, size):
    target = OUT / f"{name}.png"
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    f"--window-size={size}", "--virtual-time-budget=6000", f"--screenshot={target}", f"{base}?{query}"],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print(target.relative_to(HERE.parent.parent))


OUT.mkdir(exist_ok=True)
for i, name in enumerate(SLIDES, 1):
    shoot(name, f"slide={i}", "1080,1920")
shoot("feature-graphic", "mode=feature", "1024,500")
shoot("overview-strip", "mode=strip", "3960,1400")
server.shutdown()
