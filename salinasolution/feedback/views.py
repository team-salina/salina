 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt
from salinasolution.debug import debug
import json
import  salinasolution.var as Var
from salinasolution.feedback.models import Feedback
from salinasolution.userinfo.models import App, Manager, User
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson
import salinasolution.dataread as dataread 



'''

@csrf_exempt
def system_feedback(request):
    if request.mehtod == 'POST':
        dic = json.loads(request.raw_post_data)
        
        
        def make_instance_by_name(name):
    constructor = globals()[name]
    obj_instance = constructor()
    return obj_instance
'''



@csrf_exempt
def save_user_feedback(request):
   if request.mehtod == 'POST':
        feedback = Feedback()
       
        dic = json.loads(request.raw_post_data)
        dic_key_list = dic.keys()
        
        for key in dic_key_list:
            
            if key == 'Feedback':
                instance = pson.make_instance_by_name(key)
                instance = pson.dic_to_obj(dic[key], instance)
                instance.auto_save()
                feedback = instance
            
            elif key == 'FeedbackContext':
                instance = pson.make_instance_by_name(key)
                instance = pson.dic_to_obj(dic[key], instance)
                instance.feedback = feedback
                instance.save()
                 
        
        
    




























'''
this page is for view of feedback
this page can make user vote, evaluation, feedback
made by doo hyung
'''

#every python file has this TAG, this TAG means the path of salina framework
TAG = "feedback.views"

# request로부터 feedback 정보를 얻고, db로 저장하는 메서드
# create_feedback

def save_feedback(request):
    #convert json to python data
    #request로부터 얻어온 데이터로 dic로 전환시킨다.
    dic = json.loads(request.raw_post_data)
    print dic
    #if you execute dic_to_obj, it convert from dic to obj
    #pson.dic_to_obj 메서드를 수행하면 인자로 넣은 obj의 속성값을(property)를 채워서 반환한다.
    feed = pson.make_feed_obj(dic)
    #serialize를 수행하면 해당 객체를 json으로 만들어서 반환시킨다.
    #return_data =  serializers.serialize('json',dic)
    #debug(TAG, return_data)
    return "success"

#피드백을 만들거나 투표를 하거나 하는 기능을 하는 메서드
@csrf_exempt
def feedback(request):
    debug(TAG, "suggestion method start")
    return_data = None 
    #create feedback
    #feedback data를 생성한다.(create = post)
    if request.method == 'POST':
        debug(TAG, "POST METHOD")
        
        return_data = save_feedback(request)
        print return_data
    return HttpResponse(return_data)


def view_community_feedback(request):
    
    app_id = request.GET[Var.APP_ID]
    category = request.GET[Var.CATEGORY]
    data_num = 10
    
    if request.method == 'GET':
        debug(TAG, "POST METHOD")
        return_list = dataread.read_data(app_id, category, data_num)
        json_return_data = json.dumps(return_list)
        
    return render_to_response('index.html', {
                                                 'feedback': json_return_data,
                                                 
                                                 },
                                  context_instance=RequestContext(request))
        
    
  
    

