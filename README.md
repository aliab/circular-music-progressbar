# Android Circular Music ProgressBar
[![](https://jitpack.io/v/aliab/circular-music-progressbar.svg)](https://jitpack.io/#aliab/circular-music-progressbar)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-circular--music--progressbar-green.svg?style=true)](https://android-arsenal.com/details/1/4000)

![Hero Image](https://raw.githubusercontent.com/aliab/circular-music-progressbar/master/art/splash.jpg)


## Description

This Circular Progress bar is a designed and made for music players that need beautiful music progress bar. [Choosed for best of the day at uplabs.com](https://www.uplabs.com/posts/android-circular-music-progressbar)

## Usage

To use Circular Music ProgressBar you must add it as a dependency in your Gradle build:

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
Step 2. Add the dependency
```groovy
dependencies {
    compile 'com.github.aliab:circular-music-progressbar:v1.3.0'
}
```

Then add the view to your layout:

```xml
    <info.abdolahi.CircularMusicProgressBar
    	xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/album_art"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:layout_gravity="center"
        android:src="@drawable/maron5"
        app:border_color="#00796B"
        app:border_width="2dp"
        app:centercircle_diammterer="1"
        app:progress_color="#80CBC4"
        app:progress_startAngle="40"
	app:draw_anticlockwise="false" />
```
## XML attributes

```xml
    <info.abdolahi.CircularMusicProgressBar
        app:border_color="#00796B"
        app:border_width="2dp"
        app:centercircle_diammterer="1"
        app:progress_color="#80CBC4"
        app:progress_startAngle="40"
	app:draw_anticlockwise="true" />
```

## XML attributes

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|border_color|Color|Color.BLACK| ProgressBar background color |
|border_width|dimen|2| Thicknes of progress bar |
|centercircle_diammterer|Float|0.805f| Number between 0 and 1, that change diammeter of album art image|
|progress_color|Color|Color.BLUE| ProgressBar active state color |
|progress_startAngle|Float| 0 | Number between 0 and 360 that describe Start angle of progress bar active state |
|draw_anticlockwise|Boolean| false | If set to true, progress will be Anti-Clockwise |

## Public Methods

| Name | Description |
|:----:|:----:|
|setValue(float)| Change value of progress with animation|
|setValueWithNoAnimation(float)| Change value of progress with animation|
|setBorderProgressColor(Color)| Set progressbar active state color |
|setBorderColor(Color)| Set progressbar non-active state color |
|setProgressAnimationState(boolean)| Change state of progress value animation. set it to 'false' if you don't want any animation|
|setProgressAnimatorInterpolator(TimeInterpolator)| Change interpolator of animation to get more effect on animation|
|setOnCircularBarChangeListener(OnCircularSeekBarChangeListener listener)|Get an update if user want to seek to position or click on image|

Note: Because I extended ImageView you can use all ImageView public methods too.

Limitations
-----------
* The ScaleType is always CENTER_CROP and you'll get an exception if you try to change it. This is (currently) by design as it's perfectly fine for music album art.
* Enabling `adjustViewBounds` is not supported as this requires an unsupported ScaleType
* If you use an image loading library like Picasso or Glide, you need to disable their fade animations to avoid messed up images. For Picasso use the `noFade()` option, for Glide use `dontAnimate()`. If you want to keep the fadeIn animation, you have to fetch the image into a `Target` and apply a custom animation yourself when receiving the `Bitmap`.
* Using a `TransitionDrawable` with `CircleImageView` doesn't work properly and leads to messed up images.

## Todo

 * ~~Animation interpolator setter~~
 * ~~Animation disable/enable method~~
 * ~~Support Anti-Clockwise progress~~
 * ~~Drag to seek listener~~
 * Buffering mode animation
 * Change thickness of borderes independently
 * Support shadow/glow

## Changelog
### v1.3.0

 * Support drag to specific position
 * Some measurement bug fix

### v1.2.0

 * Support Anti-Clockwise progress

### v1.1.1

 * Fix Force Close when add view dynamically

### v1.1.0

 * Public method for change `animation interpolator`
 * Public method to disable animation

### v1.0.1

 * Remove `AllowBackup` from `manifest` of library

### v1.0.0

 * Initial release

## CREDITS
* Special Thanks to [CircleImageView](https://github.com/hdodenhof/CircleImageView).

## License
```
   
The MIT License (MIT)

Copyright (c) 2017 Ali Abdolahi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
