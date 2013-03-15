from django.db import models
from xmlrpclib import DateTime
from salinasolution.userinfo.models import User
import json

# Create your models here.
    
class Session(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    activity_name = models.CharField(max_length=50)
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
    
    
class DeviceInfo(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    os_version = models.CharField(max_length=50)
    device_name = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    app_version = models.CharField(max_length=50)
    create_date = models.DateTimeField(auto_now_add=True)

class Crash(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    exception_name = models.CharField(max_length=50)
    stacktrace = models.CharField(max_length=50)
    method_name = models.CharField(max_length=50)
    line_number = models.IntegerField()
    occur_time = models.DateTimeField(auto_now_add=True) 
    
    
    
    
    
    
    
    
    
    
