[![latest release](https://img.shields.io/badge/latest%20release-v1.0.1-green.svg?style=flat-square) ![release date](https://img.shields.io/badge/release%20date-2019.01.28-orange.svg?style=flat-square)](https://github.com/GreatApo/Amazfit-Timeline-Widget/releases/latest)
![Amazfit Timeline Widget v1.0.1](other%20files/icon.png)
# Amazfit Timeline Widget

Here is the first Calendar Events Widget for our Amazfits!



### Features
- This is a Pace/Stratos/Verge Widget
- Shows your phone calendar events
- Shows only future events and from the last 10 minutes
- Long press time at top to manually refresh the events list (provided that new data have been send from phone)

In order to work, [Amazmod](https://github.com/AmazMod/AmazMod) (both phone and service/watch) must be installed, and calendar data forwarding should be enabled.


### Bugs / To Do
- No interaction with the events items (data not used: account, description)
- Time should constantly be updated



### Download

Get a ready to use binary
 - From our [Github Releases](https://github.com/GreatApo/Amazfit-Timeline-Widget/releases/latest)

Or if you are hardcore, compile the source code with Android Studio.



### Installation
To install this widget, you will need a PC with the ADB installed. Connect your Amazfit on your PC and fire up a terminal.

```shell
adb install -r TimelineWidget.X.X.X.apk
adb shell am force-stop com.huami.watch.launcher
```

To uninstall:

```shell
adb uninstall com.dinodevs.timelinewidget
```



### Screenshots (Version 1.0.0)
![Amazfit Timeline Widget v1.0.0](other%20files/TimelineWidget-1.0.0.jpg)
![Amazfit Timeline Widget v1.0.1](other%20files/no_events.jpg)
![Amazfit Timeline Widget v1.0.1](other%20files/amazmod.jpg)



### Thanks to the Developers

This project was made possible by:

 - GreatApo - *Widget Creator* - [ [Github](https://github.com/GreatApo) | [XDA](https://forum.xda-developers.com/member.php?u=3668555) ]
 - Quinny899 - *Widget Example Creator / Springboard Settings Creator* - [ [Github](https://github.com/KieronQuinn) | [XDA](https://forum.xda-developers.com/member.php?u=3563640) ]
 - Community (testers, developers)

Some more links:

 - GreatApo's [Amazfit Calendar Widget - XDA topic](https://forum.xda-developers.com/smartwatch/amazfit/app-widget-calendar-pace-t3751889)
 - Amazmod [Amazmod Project](https://github.com/AmazMod/AmazMod)
 - Quinny's [Springboard Settings - XDA topic](https://forum.xda-developers.com/smartwatch/amazfit/app-springboard-settings-pace-rearrange-t3748651)
 - Quinny's [Widget Creation guide - XDA topic](https://forum.xda-developers.com/smartwatch/amazfit/dev-create-custom-home-screen-pages-pace-t3751731).
