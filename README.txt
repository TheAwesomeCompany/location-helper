To implement lib in your project you need to stick this simpe rules:
1) Go to your class where you want to start find location
2) First of all you need to implement BroadcastReceiver in your project. It have to listen action
LocationService.BROADCAST_LOCATION_SUCCESS, LocationService.BROADCAST_LOCATION_ERROR
3) After that to start location service you have to init LocationHelper. Just call LocationService.initLocationHelper() for it
4) Now you can start location listener via LocationService.startSendData().
5) When you will want stop location listener just call LocationService.endSendData()