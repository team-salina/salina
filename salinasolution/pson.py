import json
from salinasolution.userinfo.models import User
from salinasolution.var import Var
from salinasolution.controllog.models import Crash, DeviceInfo, Session


'''
������ �α׸� ������ ���� �ν��Ͻ��� ����� , �� ���� �ν��Ͻ��� �������  ��Ʈ�� �α׸� �����ϴ� ����
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
Ư�� obj�� ���� list�� �����Ҷ� ����ϴ� �Լ�
���� ��� saving_obj�� user= User()�� ���� �ν��Ͻ��� �� �� �ִ�.
�̷��� �ν��Ͻ��� �Բ� dic�� �ִ� �����͸� �����ϰ� ������ ����Ѵ�.
'''
def save_list_with_obj(list, target_obj, saving_obj, saving_obj_name):
    
    for dic in list:
        save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name)
              
    return list

'''
target_obj��� �ν��Ͻ��� list�� ��� dic�� �����Ҷ� ����Ѵ�.
'''
def save_list(list, target_obj):
    
    for dic in list:
        save_dic(dic, target_obj)
              
    return list
    
'''
target_obj��� �ν��Ͻ���  dic�� �����Ϳ� Ư���� obj�� �԰� �����Ҷ� ����Ѵ�.
dic���� �����ϰ��� �ϴ� �����Ͱ� ���������, saving_obj���� ���� ������ �ν��Ͻ�, saving_obj_name���� ���� ������ �ν��Ͻ��� 
�̸��� ����.
'''
def save_dic_with_obj(dic, target_obj, saving_obj, saving_obj_name):
    
    setattr(target_obj, saving_obj_name, saving_obj)
    save_dic(dic, target_obj)
    
    return target_obj

'''
obj_dic�� ����մ� ��ü��� dic�� ����մ� �����͸� �����Ҷ� ����Ѵ�.
'''
def save_dic_with_obj_dic(dic, target_obj, included_obj_dic):
    
    target_obj = dic_to_obj(included_obj_dic, target_obj)
    save_dic(dic, target_obj)
    
    return target_obj

'''
dic�� �ν��Ͻ��� �ٲٰ� �����ϰ� �����Ѵ�.
'''
def save_dic(dic, obj):
    
    return dic_to_obj(dic, obj).save()
    
'''
dic�� obj�� �ٲ��ִ� �޼���
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

    
    
    
