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

# create_feedback_for_test
def create_feedback_for_test(request):
    debug(TAG, "test_method")
    #extract data from dictionary
    user_id = request.GET[Var.USER_ID]
    device_key = request.GET[Var.DEVICE_KEY]
    category = request.GET[Var.CATEGORY]
    app_id = request.GET[Var.APP_ID]
    contents = request.GET[Var.CONTENTS]
    debug(TAG, "before_test_method")
    Feedback().auto_save_by_property(user_id, device_key, category, app_id, contents);
    debug(TAG, "end_test_method")
    #return json type feed
    return serializers.serialize('json',[0])
'''
# create_feedback
def create_feedback(request):
    #convert json to python data
    json_data = json.loads(request.raw_post_data)
    feed_obj = pson.FeedbackObject().make_object(json_data)
    feed = Feedback().auto_save_object(feed_obj)
    return serializers.serialize('json',[feed])
'''
#create suggestion feedback or vote suggestion
def feedback(request):
    '''
    debug(TAG, "suggestion method start")
    return_data = None    
    #create suggestion feedback
    if request.method == 'POST':
        debug(TAG, "POST METHOD")
        return_data = create_feedback(request)
    return HttpResponse(return_data)
    '''
    app_list = 22
    bug = 12
    return render_to_response('index.html',{'app_list':app_list,
                                            'bug':bug 
                                            
                                            
                                            },
                              context_instance = RequestContext(request)
                              )
    

