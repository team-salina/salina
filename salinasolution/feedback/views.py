 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt
from django.core.context_processors import request
from salinasolution.debug import debug
import json
from salinasolution.var import Var
from salinasolution.feedback.models import Feedback
from salinasolution.userinfo.models import App, Manager, User
from django.core import serializers
from django.http import HttpResponse
import os
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson

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
    
  
    

