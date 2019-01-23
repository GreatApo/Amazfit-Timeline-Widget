[![latest release](https://img.shields.io/badge/latest%20release-v1.0.0.Beta-green.svg?style=flat-square) ![release date](https://img.shields.io/badge/release%20date-2019.00.00-orange.svg?style=flat-square)](https://github.com/GreatApo/Amazfit-Timeline-Widget/releases/latest)

# Amazfit Pace/Stratos/Verge Timeline Widget

After [Quinny899](https://github.com/KieronQuinn)'s excellent work, we are able to make widget/apps!
So, here is the first Calendar Widget for our Amazfit Pace/Stratos!



### Features
- This is a Pace/Stratos/Verge Widget
- Shows your phone calendar event provided that they are pushed to watch by Amazmod [Amazmod Project](https://github.com/AmazMod/AmazMod)


### Bugs / To Do
- Amazmod has no on/off option (events are just pushed along battery/alarm data)
- Events are not sorted by date
- Date change indicator is similar to events = ugly (see image)
- No interaction with the events items (data not used: account, description)
- Shows all data, it should show future only (easy fixed but first events have to sorted)



### Download

Get a ready to use binary
 - From our [Github Releases](https://github.com/GreatApo/AmazfitPaceCalendarWidget/releases/latest)

Or if you are hardcore, compile the source code with Android Studio.



### Installation
To install this widget, you will need a PC with the ADB installed. Connect your Amazfit on your PC and fire up a terminal.

```shell
adb uninstall com.dinodevs.timelinewidget
adb install -r TimelineWidget.X.X.X.apk
adb shell am force-stop com.huami.watch.launcher
```



### Screenshots (Version 1.0.0 beta)
![Amazfit Timeline Widget v1.0.0](other%20files/TimelineWidget-1.0.0.jpg)



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
