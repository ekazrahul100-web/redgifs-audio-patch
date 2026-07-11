## [1.1.3](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.1.2...v1.1.3) (2026-07-11)

### 🐛 Bug Fixes

* push diagnostic patch that forces null to verify login interference ([551bbd1](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/551bbd10f328421922e8fd3604fff845952e1c92))

## [1.1.2](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.1.1...v1.1.2) (2026-07-11)

### 🐛 Bug Fixes

* remove StrictMode stub from dex to prevent possible SecurityExceptions or class loading issues ([3d2e3e4](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/3d2e3e451abdc0dd9f7350e6054f7c223860894e))

## [1.1.1](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.1.0...v1.1.1) (2026-07-11)

### 🐛 Bug Fixes

* call toLowerCase() on redgifs ID before fetching from API ([d65d93d](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/d65d93d8f851e4820d2245371c7dc6dcc7f864bf))

## [1.1.0](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.0.4...v1.1.0) (2026-07-11)

### 🐛 Bug Fixes

* remove java sources to prevent gradle compile errors since we use precompiled dex ([8498be8](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/8498be8b494f3b11c370cda97ea2df0a53cf9ff4))

### ✨ New Features

* implement correct RedGifs Auth token fetch and proper class injection via extendWith ([c533356](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/c533356b99dae0810556b431de5e587beb960966))

## [1.0.4](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.0.3...v1.0.4) (2026-07-11)

### 🐛 Bug Fixes

* bundle RedGifsHelper as smali in resources to fix NoClassDefFoundError ([7a96154](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/7a961546cf5811ce73be1460b3067df8647c8383))

## [1.0.3](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.0.2...v1.0.3) (2026-07-11)

### 🐛 Bug Fixes

* bundle RedGifsHelper as smali to fix NoClassDefFoundError runtime crash ([bb67c9c](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/bb67c9cdf9b4ed0160cee55ae5d332fd578c5136))

## [1.0.2](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.0.1...v1.0.2) (2026-07-11)

### 🐛 Bug Fixes

* remove org.json dependency and use Regex ([6f0d2b6](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/6f0d2b60571d8b0bbfecdb06fb225970384b290f))
* Rewrite patch to hook RedditVideo constructor and fetch RedGifs HD mp4 ([f59c15e](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/f59c15ee8c2ee0f52675dcb1d41707763d1c8f6c))

## [1.0.1](https://github.com/ekazrahul100-web/redgifs-audio-patch/compare/v1.0.0...v1.0.1) (2026-07-11)

### 🐛 Bug Fixes

* resolve compile errors from invalid ReVanced imports ([13a1950](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/13a1950ecc9192aae90133f2af730bc390417b05))
* rewrite patch using correct Morphe DSL and precise hook point ([469f940](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/469f94083e702b66daf98eda2155cba6ce3a2395))

## 1.0.0 (2026-07-11)

### 🐛 Bug Fixes

* migrate patch to Morphe DSL ([94c5038](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/94c50382aaae8737ca4c2ab50d4ca4a7cd902eb3))
* update imports to use Morphe patcher ([c9252f0](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/c9252f0f31ce11436c6d1ec147abf764b5bb6a49))

### ✨ New Features

* add RedGifs audio fix patch ([9f45e14](https://github.com/ekazrahul100-web/redgifs-audio-patch/commit/9f45e149117cf8b4d2a816ebe545b6413fc3cf95))
