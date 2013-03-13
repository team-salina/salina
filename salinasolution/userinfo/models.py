from django.db import models
from salinasolution.debug import debug
from django.db.models.deletion import CASCADE
from MySQLdb.constants.FLAG import UNIQUE_KEY

# Create your models here.



#this is for app
class App(models.Model):
    
    app_id = models.CharField(max_length = 50, primary_key = True)
    app_name = models.CharField(max_length = 50)
    description = models.CharField(max_length = 50)
    create_date = models.DateField(auto_now_add = True)
    icon_url = models.URLField()
    version = models.CharField(max_length = 50)    
    
class User(models.Model):
    
    user_id = models.CharField(max_length = 50)
    device_key = models.CharField(max_length = 5, primary_key = True)
    
    def auto_save(self, user_id, device_key):
        udk = User(user_id = user_id, device_key = device_key)
        udk.save()        
        return udk
    
#this is for manager    
class Manager(models.Model):
    
    app_id = models.ForeignKey(App, primary_key = True)
    manager_id = models.CharField(max_length = 50)

    
    
    
     