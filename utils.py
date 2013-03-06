import md5
'''
Created on 2013. 3. 4.

@author: dh
'''



class Userkey:
    def make_hashkey(self, uniqueid):
        m = md5.new()
        m.update(uniqueid)
        return m.digest()
