'''
Created on 2013. 3. 7.

@author: du hyeong
'''
class Var:
    # this variable for categories
    QUESTION = 1
    SUGGESTION = 2
    PROBLEM = 3
    PRAISE = 4
    REPLY = 5
    ANSWER = 6
    
    CATEGORIES = (
                  (QUESTION, 'question'),
                  (SUGGESTION, 'suggestion'),
                  (PROBLEM, 'problem'),
                  (PRAISE, 'praise'),
                  (REPLY, 'reply'),
                  (ANSWER, 'answer'),
                  )
    
    
    
    
    # this variable for key_mapping
    USER_ID = "user_id"
    DEVICE_KEY = "device_key"
    APP_ID = "app_id"
    CONTENTS = "contents"
    PRAISE_SCORE = "prase_score"
    CATEGORY = "category"
    # this variable for main_admin_page
    MANAGER_ID = "manager_id"
    
    
    #this is for controllog
    SESSION = 'session'
    DEVICE_INFO = 'device_info'
    CRASH = 'crash'
    
    ACTIVITY_NAME = "activity_name"
    START_TIME = "start_time"
    END_TIME = "END_TIME"
    
    OS_VERSION = "OS_VERSION"
    DEVICE_NAME = "DEVICE_NAME"
    COUNTRY = "COUNTRY"
    APP_VERSION = "APP_VERSION"
    CREATE_DATE = "CREATE_DATE"
    
    EXCEPTION_NAME = "exception_name"
    STACKTRACE = "STACKTRACE"
    METHOD_NAME = "METHOD_NAME"
    LINE_NUMBER = "LINE_NUMBER"
    OCCUR_TIME = "OCCUR_TIME"
    
    
    
    
    
    # ETC
    INIT_SCORE = 0


