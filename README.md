# SublimeNavBar

... is an example project that shows how to place any kind of view behind Android's navigation bar. It can also be easily adapted to place the view behind the status bar. 

Walkthrough
-----------
Following gif has been taken from the app: 

<p align="center">
    <img src="https://github.com/vikramkakkar/SublimeNavBar/blob/master/img/sublime_nav_bar.gif?raw=true" width="300" height="533" />
</p>

The example requires the following permission:

```
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

On Marshmallow & above, you also need the following runtime permission:

```
// example shows the usage
Settings.ACTION_MANAGE_OVERLAY_PERMISSION
```

Once this runtime permission is granted, `SublimeNavBarService` will need to be restarted. 

Components
----------

**InitializationAct:** Shows how to check for the required runtime permission & seek it if it hasn't been granted.

**SublimeNavBarService:** An extension of [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService.html) that manages the addition & removal of a view to the [Window](https://developer.android.com/reference/android/view/Window.html). It also manages the position of this view,
listens to [AccessibilityEvents](https://developer.android.com/reference/android/view/accessibility/AccessibilityEvent.html), and displays information above the active application.

Acknowledgements
----------------

The library uses [Picasso](http://square.github.io/picasso/), released under Apache Licence, version 2.0. 

License
-------
    Copyright (c) 2016 Vikram Kakkar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.