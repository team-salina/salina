 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()



urlpatterns = patterns('controllog.views',
    # Examples:
    # url(r'^$', 'salinasolution.views.home', name='home'),
    # url(r'^salinasolution/', include('salinasolution.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^save_system_feedback/$', 'save_system_feedback'),
    url(r'^device_key/$', 'send_device_key'),
    
    
    
)