# Sample-Location-Tracking-With-Activity-Recognition
Is a simple application which will track location and ActivityRecognition using a started service and an IntentService respectively. The idea to create a different service is to maintain de-coupling between location tracking and ActivityRecognition.

With custom marker user will easily be able to Detact the Type of activity currently going on: where activity with highest % accuracy will be shown on the custom marker. This marker will be updated each time when there is change in the accuracy of detected activities.

  - LocationRequesterService:
    This service will be started every time using AlarmManager with a PendingIntent and will be started on given time interval.
    
  - RecognitionService:
    This service will be used for: 
    1. Detecting all the activities in decending order of their accuracy.
    2. Shoot a broadcast when all the activities have been detected.
