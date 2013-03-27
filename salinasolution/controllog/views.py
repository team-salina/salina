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
# Create your views here.

TAG = "controllog.views"

@csrf_exempt
def controllog(request):
    debug(TAG, "start method")
    return_data = None
    
    if request.method == 'POST' :
        
        debug(TAG, "start method")
        print request.raw_post_data
        dic = json.loads(request.raw_post_data)
        pson.save_control_log(dic)
        
         
    return HttpResponse(return_data)
    
    
    
    