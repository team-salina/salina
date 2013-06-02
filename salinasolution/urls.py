 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url
from django.contrib import admin
import settings
 
 
 
admin.autodiscover()



urlpatterns = patterns('',
                       
    url(r'^admin/', include(admin.site.urls)),
    url(r'^feedback/', include('feedback.urls')),
    url(r'^controllog/', include('controllog.urls')),
    url(r'^userinfo/', include('userinfo.urls')),
    url(r'^login/$', 'django.contrib.auth.views.login'),
    url(r'^$', 'django.contrib.auth.views.login'),
    
     
)


urlpatterns += patterns('',
    
     url(r'^static/(?P<path>.*)$', 'django.views.static.serve', {
        'document_root': settings.STATIC_ROOT,
    }),
                        
)
