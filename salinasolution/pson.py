 #!/usr/bin/python
# -*- coding: utf-8 -*-
import json
from salinasolution.userinfo.models import User
from salinasolution.var import Var
from salinasolution.controllog.models import Crash, DeviceInfo, Session


'''
request를 받아 사용자 조작로그(유저,세선,버그)를 저장하는 메서드
'''
def save_control_logs(request):
    
    dic = json.loads(request.raw_post_data)
    
    user = User()
    user = save_dic(dic, user)
    key_list = dic.key()
    
    for key_name in key_list:
        constructor = globals()[key_name]
        obj_instance = constructor()
        obj_list = dic[key_name]
        save_list_with_obj(obj_list, obj_instance, user, 'user')
        
'''
dic를 담고 있는 list를 저장할 때 사용, 추가적으로 obj으로 담아서 자장할 수 있음
'''
def save_list_with_obj(list, target_obj, saving_obj, saving_obj_name):
    
    for dic in list:
        
        save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name)
              
    return list

'''
dic를 담고 있는 list를 저장할 때 사용
'''
def save_list(list, target_obj):
    
    for dic in list:
        save_dic(dic, target_obj)
              
    return list
    
'''
dic를 saving obj와 함게 저장
'''
def save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name):
    
    setattr(target_obj, saving_obj_name, saving_obj)
    save_dic(dic, target_obj)
    
    return target_obj


def save_dic_with_obj_dic(dic, target_obj, included_obj_dic):
    
    target_obj = dic_to_obj(included_obj_dic, target_obj)
    save_dic(dic, target_obj)
    
    return target_obj


'''
dic을 obj로 바꿔서 저장하고 리턴
'''
def save_dic(dic, obj):
    
    return dic_to_obj(dic, obj).save()
    
'''
dic을 obj로 바꿔서 리턴
'''
def dic_to_obj(dic, obj):
    
    dic_key_list = dic.keys()
    # if obj instance have user model, it is saved
    # but if user model doesn't exist in user, it will not save
    for key in dic_key_list :
        setattr(obj, key, dic[key])
             
    return obj






























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

    
    
    
