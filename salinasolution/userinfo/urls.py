 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url
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
     url(r'^main_admin/$', 'view_admin'),
     url(r'^main_admin/index.html', 'view_admin'),
     url(r'^main_admin/developer.html', 'view_developer'),
     url(r'^main_admin/app-home.html', 'view_app_home'),
     url(r'^main_admin/feedback.html', 'view_feedback'),
     url(r'^main_admin/register.html', 'view_register'),
     url(r'^main_admin/download.htm', 'view_contact'),
     url(r'^main_admin/about.html', 'view_about'),
     

)




