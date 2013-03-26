 #!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
#console창에 로깅하기 위한 debug 메서드
def debug(key, value):
    '''
    or repr
    '''
    
    #print_value = key + " " + value
    msg =  str(key) + " : " + str(value)
    print >> sys.stderr, msg
    
    #print print_value
        
