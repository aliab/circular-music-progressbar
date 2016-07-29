# Android Circular Music ProgressBar
[![](https://jitpack.io/v/aliab/circular-music-progressbar.svg)](https://jitpack.io/#aliab/circular-music-progressbar)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-circular--music--progressbar-green.svg?style=true)](https://android-arsenal.com/details/1/4000)

![Hero Image](https://raw.githubusercontent.com/aliab/circular-music-progressbar/master/art/splash.jpg)

## Description

This Circular Progress bar is a designed and made for music players that need beautiful music progress bar.

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
    compile 'com.github.aliab:circular-music-progressbar:v1.0'
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
        app:progress_startAngle="40" />
```
## XML attributes

```xml
    <info.abdolahi.CircularMusicProgressBar
    	...
        app:border_color="#00796B"
        app:border_width="2dp"
        app:centercircle_diammterer="1"
        app:progress_color="#80CBC4"
        app:progress_startAngle="40"
        ...
         />
```

## XML attributes

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|border_color|Color|Color.BLACK| ProgressBar background color |
|border_width|dimen|2| Thicknes of progress bar |
|centercircle_diammterer|Float|0.805f| Number between 0 and 1, that change diammeter of album art image|
|progress_color|Color|Color.BLUE| ProgressBar active state color |
|progress_startAngle|Float| 0 | Number between 0 and 360 that describe Start angle of progress bar active state |

## Public Methods

| Name | Description |
|:----:|:----:|:-------:|:-----------:|
|setValue(float)| change value of progress with animation|
|setBorderProgressColor(Color)| set progressbar active state color |
|setBorderColor(Color)| set progressbar non-active state color |

Note: Because I extended ImageView you can use all ImageView public methods too.

Limitations
-----------
* The ScaleType is always CENTER_CROP and you'll get an exception if you try to change it. This is (currently) by design as it's perfectly fine for music album art.
* Enabling `adjustViewBounds` is not supported as this requires an unsupported ScaleType
* If you use an image loading library like Picasso or Glide, you need to disable their fade animations to avoid messed up images. For Picasso use the `noFade()` option, for Glide use `dontAnimate()`. If you want to keep the fadeIn animation, you have to fetch the image into a `Target` and apply a custom animation yourself when receiving the `Bitmap`.
* Using a `TransitionDrawable` with `CircleImageView` doesn't work properly and leads to messed up images.

## Todo

 * Animation interpolator setter
 * Animation disable/enable method
 * Buffering mode animation
 * Drag to seek listener
 * change thickness of borderes independently
 * support shadow/glow

## Changelog

### v1.0.0

 * Initial release

## CREDITS
* Special Thanks to [CircleImageView](https://github.com/hdodenhof/CircleImageView).

## License
```
   Copyright (C) 2016 Ali Abdolahi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
