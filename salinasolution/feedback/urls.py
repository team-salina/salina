 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()
'''
http://61.43.139.106:8000/feedback/view_my_feedback/?app_id=noon_date&device_key=123
'''
urlpatterns = patterns('feedback.views',
    # Examples:
    # url(r'^$', 'salinasolution.views.home', name='home'),
    # url(r'^salinasolution/', include('salinasolution.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
     #url(r'^question', 'feedback'),
     #url(r'^suggestion', 'feedback'),
     #url(r'^problem', 'feedback'),
     #url(r'^praise', 'feedback'),
     #url(r'^app/$', 'view_app'),
     url(r'^save_user_feedback/$', 'save_user_feedback'),
     url(r'^view_my_feedback/$', 'view_my_feedback'),
     
     url(r'^view_feedback_detail/$', 'view_feedback_detail'),
     url(r'^view_feedbacks/$', 'view_feedbacks'),
     url(r'^view_write_reply/$', 'view_write_reply'),
     
     
     
     
    
)
