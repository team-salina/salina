'''
Created on 2013. 3. 5.

@author: so
'''
from django.contrib import admin
from FeedbackSolution.controllog.models import AppInfo, Crashes, DeviceInfo, MetaUserInfo, Session, User 

admin.site.register(AppInfo)
admin.site.register(Crashes)
admin.site.register(DeviceInfo)
admin.site.register(MetaUserInfo)
admin.site.register(Session)
admin.site.register(User)

