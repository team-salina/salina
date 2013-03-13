import json
from salinasolution.userinfo.models import User
from salinasolution.var import Var
from salinasolution.controllog.models import Crash, DeviceInfo, Session


'''
저장할 로그를 가져와 유저 인스턴스를 만들고 , 이 유저 인스턴스를 기반으로  컨트롤 로그를 저장하는 연산
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
특정 obj와 같이 list을 저장할때 사용하는 함수
예를 들어 saving_obj는 user= User()와 같은 인스턴스가 될 수 있다.
이러한 인스턴스와 함께 dic에 있는 데이터를 저장하고 싶을때 사용한다.
'''
def save_list_with_obj(list, target_obj, saving_obj, saving_obj_name):
    
    for dic in list:
        save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name)
              
    return list

'''
target_obj라는 인스턴스에 list에 담긴 dic를 저장할때 사용한다.
'''
def save_list(list, target_obj):
    
    for dic in list:
        save_dic(dic, target_obj)
              
    return list
    
'''
target_obj라는 인스턴스에  dic의 데이터와 특정한 obj를 함게 저장할때 사용한다.
dic에는 저장하고자 하는 데이터가 들어있으며, saving_obj에는 같이 저장할 인스턴스, saving_obj_name에는 같이 저장할 인스턴스의 
이름이 들어간다.
'''
def save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name):
    
    setattr(target_obj, saving_obj_name, saving_obj)
    save_dic(dic, target_obj)
    
    return target_obj

'''
obj_dic에 들어잇는 객체들과 dic에 들어잇는 데이터를 저장할때 사용한다.
'''
def save_dic_with_obj_dic(dic, target_obj, included_obj_dic):
    
    target_obj = dic_to_obj(included_obj_dic, target_obj)
    save_dic(dic, target_obj)
    
    return target_obj

'''
dic을 인스턴스로 바꾸고 저장하고 리턴한다.
'''
def save_dic(dic, obj):
    
    return dic_to_obj(dic, obj).save()
    
'''
dic를 obj로 바꿔주는 메서드
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

    
    
    
