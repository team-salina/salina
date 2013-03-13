from django.db.models import Count
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution.debug import debug
from salinasolution.userinfo.models import Manager
from salinasolution.var import Var
# Create your views here.

TAG = "adminpage.views"

def view_app(request):
    debug(TAG, "view_app_method")
    if request.method == 'GET':
        user_id = request.GET[Var.USER_ID]
        
        manager_info = Manager.objects.get(user_id = user_id)
        app_info = manager_info.managerappinfo_set.get()
        app_list = []
        
        for feedback in app_info.feedback_set.all:
            app_info = feedback.group_by('category').annotate(ccount = Count('category'))
            app_list.append(app_info)
            
    return render_to_response('', {'app_list':app_list},                              
                              context_instance = RequestContext(request))
    
    
    
def view_admin(request):
    debug(TAG, "view_app_method")
    if request.method == 'GET':
        manager_id = request.GET[Var.MANAGER_ID]
        app_id = request.GET[Var.APP_ID]

        manager = Manager.objects.get(manager_id = manager_id)
        manager= manager.managerapp_set.get()
        
        
        
        
        
        
        
        
        
        
        
            
            

        
        
        
    
    