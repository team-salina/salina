 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db import models
from salinasolution.debug import debug
from django.db.models.deletion import CASCADE
from MySQLdb.constants.FLAG import UNIQUE_KEY

'''
Created on 2013. 3. 7.

@author: doo hyung

유저, 앱관리자(개발자), 애플리케이션등의 메타정보를 저장하는 모델을 정의한 모듈이다.
'''

'''
 Create your models here.
 app정보를 갖고 있는 테이블이다.
'''
class App(models.Model):
    
    app_id = models.CharField(max_length = 50, primary_key = True)
    app_name = models.CharField(max_length = 50)
    description = models.CharField(max_length = 50)
    create_date = models.DateField(auto_now_add = True)
    icon_url = models.URLField()
    version = models.CharField(max_length = 50)    
    
'''
user(애플리케이션 사용자에 대한 정보를 나타낸 테이블이다.)
device_key는 각 기기마다 잇는 유일한 정보를 나타내고,  
user_id는 여러기기를 갖고 있는 사용자인 경우, 하나의 사용자라는 걸 파악하기 위해 사용할 open id이다.
'''
class User(models.Model):
    
    user_id = models.CharField(max_length = 50)
    device_key = models.CharField(max_length = 5, primary_key = True)
    
    def auto_save(self, user_id, device_key):
        udk = User(user_id = user_id, device_key = device_key)
        udk.save()        
        return udk
'''    
manager는 앱 관리자(개발자)를 나타내는 테이블이다.
app id는 관리자가 관리하는 app의 id이다.
''' 
class Manager(models.Model):
    
    app_id = models.ForeignKey(App, primary_key = True)
    manager_id = models.CharField(max_length = 50)

    
    
    
     