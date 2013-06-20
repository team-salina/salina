 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url
#from django.contrib.auth.views import login
'''
Created on 2013. 3. 7.

@author: doo hyung
'''
urlpatterns = patterns('userinfo.views',
    # Examples:
    # url(r'^$', 'salinasolution.views.home', name='home'),
    # url(r'^salinasolution/', include('salinasolution.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
     #url(r'^app/$', 'view_app'),
     #http://61.43.139.106:8000/userinfo/main_admin/app-home.html
     #register_page
     #url(r'^login/$', 'django.contrib.auth.views.login'),
     #url(r'^main_admin/login.html', 'django.contrib.auth.views.login'),
     url(r'^register.html', 'register_page'),
     url(r'^index.html', 'view_admin'),
     url(r'^developer.html', 'view_developer'),
     url(r'^app-home.html', 'view_app_home'),
     url(r'^feedback.html', 'view_feedback'),
     url(r'^download.htm', 'view_contact'),
     url(r'^about.html', 'view_about'),
     url(r'^real_voice.html', 'view_real_voice'),
     url(r'^advanced.html', 'view_advanced'),
     url(r'^insight.html', 'view_insight'),
     
)




