package com.trance.common.util;


public class HashAlgorithms {

    /**   
     * 改进的32位FNV算法1   
     * @param data 数组   
     * @return int值   
     */   
    public static int fnvHash(byte[] data) {
        final int p = 16777619;    
        int hash = (int)2166136261L;   
        
        for(byte b: data)    
            hash = (hash ^ b) * p; 
        
        hash += hash << 13;    
        hash ^= hash >> 7;    
        hash += hash << 3;    
        hash ^= hash >> 17;    
        hash += hash << 5;   
        
        return hash;    
    }
	
}
