from django.conf.urls.defaults import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',   
    url(r'^admin/$',include(admin.site.urls)),
    url(r'^feedback/$',include('feedback.urls')),
    '''
    url(r'^adminpage/$',include('adminpage.urls')),    
    url(r'^controllog/$',include('controllog.urls')),
    '''
)
