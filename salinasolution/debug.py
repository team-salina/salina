 #!/usr/bin/python
# -*- coding: utf-8 -*-
#console창에 로깅하기 위한 debug 메서드
def debug(key, value):
    '''
    or repr
    '''
    value = str(value)
    print_value = key + " \\ " + value
    print print_value
        
