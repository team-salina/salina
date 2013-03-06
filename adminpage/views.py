# Create your views here.
from django.template import RequestContext
from django.http import HttpResponse
from django.shortcuts import get_object_or_404, render_to_response
from django.utils import timesince
#create Class from my model
from FeedbackSolution.feedback.models import Evaluation,Feedback, Reply, Vote, QUESTION, SUGGESTION
from FeedbackSolution.controllog.models import MetaUserInfo, User, AppInfo, Session, DeviceInfo, Crashes
  
'''
    this page is used by admin
    made by dh
'''
#when you go to admin page, administrator must select his own app
def admin_app(request):
    '''
    if request.method == 'GET' :
        user_id = request.GET['user_id']
    '''
    user_id = 'kkk'
    app_info = AppInfo.objects.get(app_info__user_id = user_id)
    return render_to_response('admin/app.html',{'app_info' : app_info},
                                  context_instance = RequestContext(request))
#when you go to admin page, you can see this page at first
def admin_main(request):
    '''
    if request.method == 'GET' :
        app_id = request.GET['app_id']
        question = Feedback.objects.get(question__category = QUESTION, question__app_id = app_id)
        suggestion = Feedback.objects.get(question__category = SUGGESTION, question__app_id = app_id)
        
        problem = Feedback.objects.get(question__category = , question__app_id = app_id)
        
    '''    
        
        
    
    
    
