# Create your views here.
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

# create_feedback
def save_feedback(request):
    #convert json to python data
    dic = json.loads(request.raw_post_data)
    feed =  pson.dic_to_obj(dic, Feedback())
    feed.auto_save_by_object(feed)
    return serializers.serialize('json',[feed])

#create suggestion feedback or vote suggestion
def feedback(request):
    
    debug(TAG, "suggestion method start")
    return_data = None    
    #create suggestion feedback
    if request.method == 'POST':
        debug(TAG, "POST METHOD")
        return_data = save_feedback(request)
    return HttpResponse(return_data)
    
  
    

