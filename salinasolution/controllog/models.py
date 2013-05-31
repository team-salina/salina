 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db import models
from xmlrpclib import DateTime
from salinasolution.userinfo.models import AppUser
import json
import time
from datetime import datetime

'''
Created on 2013. 3. 7.

@author: doo hyung

controllog 부분은  사용자의 로그를 수집하는 부분으로  session(유저,세션정보), deviceinfo(기기정보), crash(버그정보)로 나뉘어져 있다.
'''

# Create your models here.

'''
세션 정보는 하나의 액티비티당 사용자가 얼마나 머물렀는지 파악하는 정보이다.
activity_name에는 사용한 액티비티가 들어가고, start_time은 액티비티를 들어온 시간, 
end_time은 액티비티를 나간 시간을 기록한다.
'''  

date_format = "%Y-%m-%d %H:%M:%S"

class Session(models.Model):
    
    user = models.ForeignKey(AppUser)
    activity_name = models.CharField(max_length=50)
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
    app_id = models.CharField(max_length = 50)
     
    def auto_save(self):
        try:
            self.user = AppUser().auto_save(self.user.user_id, self.user.device_key)
            self.start_time = datetime.strptime(self.start_time,"%Y-%m-%d %H:%M:%S")
            self.end_time = datetime.strptime(self.end_time,"%Y-%m-%d %H:%M:%S")
            self.save()
        except Exception as e:
            print e    
        return self
    
'''
device info는 유저가 사용하는 기기에 대한 총괄적인 정보를 나타낸다.
기기정보에는  os version, devce_name(기기이름), 국가정보, 위도 , 경도, 
현재 배포된 애플리케이션 버전, 해당 정보가 저장된 시점이 기록된다. 
단한번만 보내도록 시키자 준영이한테
'''  
class DeviceInfo(models.Model):
    
    user = models.ForeignKey(AppUser,  primary_key = True)
    os_version = models.CharField(max_length=50)
    device_name = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    app_version = models.CharField(max_length=50)
    
    network_type = models.CharField(max_length=50)
    locale_language = models.CharField(max_length=50)
    device_country = models.CharField(max_length=50)
    network_carrier = models.CharField(max_length=50)
    network_type = models.CharField(max_length=50)
    latitude = models.FloatField()
    longitude  = models.FloatField()
    device_manufacturer = models.CharField(max_length=50)
    device_model = models.CharField(max_length=50)
    app_version = models.CharField(max_length=50)
    
    create_date = models.DateTimeField()
    
    def auto_save(self):
        self.user = AppUser().auto_save(self.user_id, self.device_key)
        #String을 정해진 포맷으로 바꾸는 부분
        self.latitude = float(self.latitude)
        self.longitude = float(self.longitude)
        #String을 정해진 포맷으로 바꾸는 부분
        self.create_date = datetime.strptime(self.create_date,"%Y-%m-%d %H:%M:%S")        
        
        self.save()
        return self
    
    
    

    


    
    
    
    
    
    
    
    
    
