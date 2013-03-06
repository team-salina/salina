from django.db import models

# Create your models here.


# Create your models here.
class MetaUserInfo(models.Model):
    user_key = models.CharField(max_length = 100)
    app_id = models.CharField(max_length = 100)
    user_id   = models.CharField(max_length = 100)
    
class User(models.Model):
    user_id = models.CharField(max_length = 100)
    password = models.CharField(max_length = 100)
    nickname  = models.CharField(max_length = 100)
    idType = models.CharField(max_length = 100)
    
class AppInfo(models.Model):
    app_id = models.CharField(max_length = 100)
    app_name = models.CharField(max_length = 100)
    user_id = models.CharField(max_length = 100)
    description = models.TextField()
    pub_date = models.DateField(verbose_name = None, name = None, auto_now = True, auto_now_add = False)
    icon_url = models.URLField()
    version = models.CharField(max_length = 100)
    
class Session(models.Model):
    user_key = models.CharField(max_length = 100)
    app_id = models.CharField(max_length = 100)
    activity_name = models.CharField(max_length = 100)
    start_time = models.DateField(verbose_name = None, name = None, auto_now = True, auto_now_add = False)
    end_time = models.DateField()
    
class DeviceInfo(models.Model):
    user_key = models.CharField(max_length = 100)
    app_id = models.CharField(max_length = 100)
    os_version = models.CharField(max_length = 100)
    device_model = models.CharField(max_length = 100)
    country = models.CharField(max_length = 100)
    app_version = models.CharField(max_length = 100)
    start_time = models.DateField(verbose_name = None, name = None, auto_now = True, auto_now_add = False)
    
class Crashes(models.Model):
    user_key = models.CharField(max_length = 100)
    app_id = models.CharField(max_length = 100)
    exception_name = models.CharField(max_length = 100)
    stack_trace = models.CharField(max_length = 100)
    method_name = models.CharField(max_length = 100)
    line_number = models.CharField(max_length = 100)
    occur_time = models.DateField()
    