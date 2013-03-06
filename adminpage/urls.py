from django.conf.urls.defaults import patterns, include, url


# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('admin.views',

    url(r'^admin/app/$','admin_app')  ,                     
    url(r'^admin/main/$','admin_main'),
    
   
)
