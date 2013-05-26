 #!/usr/bin/python
# -*- coding: utf-8 -*-
import json
import var
from salinasolution.userinfo.models import User
from salinasolution.controllog.models import DeviceInfo, Session
from salinasolution.feedback.models import Feedback
from salinasolution.debug import debug 
from django.db import transaction


TAG = "pson"

def make_instance_by_name(name):
    constructor = globals()[name]
    obj_instance = constructor()
    return obj_instance

def save_control_log(dic):
    
    key_list = dic.keys()
    
    for key_name in key_list:
        obj_instance = make_instance_by_name(key_name)
        obj_list = dic[key_name]
        save_obj_list(obj_list, obj_instance)
        
def save_obj_list(obj_list, obj_instance):
    
    with transaction.commit_on_success():
        for dic in obj_list:
            try :
                made_obj = make_obj(dic, obj_instance)
                made_obj.auto_save()
            except Exception as e:
                print e
    
def make_obj(dic, obj):
    
   user = dic_to_obj(dic, User())
   debug(TAG + " make_obj user.dev_key : ", user.device_key)
   setattr(obj, "user", user)
   debug(TAG + " make_obj obj.user.dev_key : ", obj.user.device_key)
   obj = dic_to_obj(dic, obj)
    
   return obj


def dic_to_obj(dic, obj):
    METHOD_TAG = " dic_to_obj "
    dic_key_list = dic.keys()
    # if obj instance have user model, it is saved
    # but if user model doesn't exist in user, it will not save
    debug(TAG + METHOD_TAG + "obj name : ", obj.__class__.__name__)
    for key in dic_key_list :
        #if hasattr(obj, key) :
            setattr(obj, key, dic[key])
    return obj

def make_feed_obj(dic):
    feed = Feedback()
    user = dic_to_obj(dic, User())
    setattr(feed, "user", user)
    feed = dic_to_obj(dic, feed)
    print feed.app_id
    return feed

def get_obj_instance_name(obj):
    
    class_name = str(obj.__class__.__name__)
    listed_obj = list(class_name)
    
    count = 0;
    return_str = ""
    
    for char in listed_obj :
        if (ord(char) <= ord('Z')) & (ord(char) >= ord('A')):
            if count == 0 :
                char = char.lower()
            else:    
                char = "_" + char.lower()
        
        count = count + 1    
        return_str += char
     
    return return_str
        



'''
def makeObj(request, obj):
        
    dic = json.loads(request.post_raw_data)
    dic_key_list = dic.keys()
    
    user = User()
    user = jsonToObj(request, user)
    user.save()    
        
    for key in dic_key_list :
        setattr(obj, key, dic[key])
             
    return obj





def jsonToObj(request, obj):
        
    dic = json.loads(request.post_raw_data)
    dic_key_list = dic.keys()
        
    # if obj instance have user model, it is saved
    # but if user model doesn't exist in user, it will not save
    for key in dic_key_list :
        setattr(obj, key, dic[key])
             
    return obj
    
'''

    
    
    
