 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db import models
from xmlrpclib import DateTime
from salinasolution.userinfo.models import User
import json

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
class Session(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    activity_name = models.CharField(max_length=50)
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
     
    def auto_save(self):
        self.user = User().auto_save(self.user_id, self.device_key)
        self.save()
        return self
    
'''
device info는 유저가 사용하는 기기에 대한 총괄적인 정보를 나타낸다.
기기정보에는  os version, devce_name(기기이름), 국가정보, 위도 , 경도, 
현재 배포된 애플리케이션 버전, 해당 정보가 저장된 시점이 기록된다. 
'''  
class DeviceInfo(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    os_version = models.CharField(max_length=50)
    device_name = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    app_version = models.CharField(max_length=50)
    create_date = models.DateTimeField(auto_now_add=True)
    
    def auto_save(self):
        self.user = User().auto_save(self.user_id, self.device_key)
        self.save()
        return self


'''
crash 정보는 해당 유저가 겪은 버그정보에 대해서 상세하게 나타내주는 역할을 한다.
여기에 포함되는 정보는 예외의 이름, 예외 발생시의 스택정보, 발생된 메소드 이름, 
발생된 에러의  line number, 발생된 시간등이 나타난다. 
'''
class Crash(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    exception_name = models.CharField(max_length=50)
    stacktrace = models.CharField(max_length=50)
    method_name = models.CharField(max_length=50)
    line_number = models.IntegerField()
    occur_time = models.DateTimeField(auto_now_add=True) 
    
    def auto_save(self):
        self.user = User().auto_save(self.user_id, self.device_key)
        self.save()
        return self
    
    
    
    
    
    
    
    
    
    
