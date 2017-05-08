/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;



public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        if(s.equals("")){
            return;
        }
        if(children.isEmpty()){
            children.put(s,new TrieNode());
            children.get(s).isWord = true;
            return;
        }
        TrieNode cur = null;
        Set<String> allkey = children.keySet();
        String word = "";
        for(String key : allkey){
            if(key.charAt(0) == s.charAt(0)){
                cur = children.get(key);
                word = key;
                break;
            }
        }
        if(cur == null){
            children.put(s, new TrieNode());
            children.get(s).isWord = true;
        }
        else{
            String sub1 = "";
            String sub2 = "";
            String sub3 = "";
            int index = 0;
            while(index < word.length() && index < s.length()&& word.charAt(index) == s.charAt(index)){
                sub1 += word.charAt(index);
                index ++;
            }
            if(index == word.length()){
                while(index < s.length()){
                    sub3 += s.charAt(index);
                    index ++;
                }
                children.get(word).add(sub3);
                return;
            }
            while(index < word.length() || index < s.length()){
                if(index < word.length()){
                    sub2 += word.charAt(index);

                }
                if(index < s.length()){
                    sub3 += s.charAt(index);

                }
                index ++;
            }
            TrieNode n = new TrieNode();
            n.children.put(sub2, cur);
            children.put(sub1,n);
            children.remove((word));
            children.get(sub1).isWord = false;
            n.add(sub3);
        }

    }

    public boolean isWord(String s) {
        int start = 0;
        int index = 0;
        String prefix = s.substring(index,index+1);
        TrieNode curNode = this;
        while(index < s.length()-1){
            if(curNode.children.containsKey(prefix)){
                curNode = curNode.children.get(prefix);
                index++;
                start = index;

                prefix = s.substring(index, index+1);
            }
            else {
                index++;
                prefix = s.substring(start, index+1);
            }
        }
        if(curNode.children.containsKey(prefix)&&curNode.children.get(prefix).isWord){
            return true;
        }
        else{
            return false;
        }
    }

    //is parameter prefix's length is odd, find a word with the same prefix that is also odd,
    //else find a word that has length even and with the same prefix
    public String getAnyWordStartingWith(String s) {
        String word = "";
        int start = 0;
        int index = 0;
        String prefix = s.substring(index,index+1);
        TrieNode curNode = this;
        while(index < s.length()-1){
            if(curNode.children.containsKey(prefix)){
                curNode = curNode.children.get(prefix);
                word += prefix;
                index++;
                start = index;

                prefix = s.substring(index, index+1);
            }
            else {
                index++;
                prefix = s.substring(start, index+1);
            }
        }
        if(curNode.children.containsKey(prefix)||findCommonPrefix(prefix,curNode.children.keySet())!=null){
            if(curNode.children.containsKey(prefix)){
                word += prefix;
                curNode = curNode.children.get(prefix);
                Set<String> listOfPrefix = curNode.children.keySet();
                String nextPrefix = listOfPrefix.iterator().next();
                curNode = curNode.children.get(nextPrefix);
                word += nextPrefix;
                while(curNode.isWord == false){
                    listOfPrefix = curNode.children.keySet();
                    nextPrefix = listOfPrefix.iterator().next();
                    curNode = curNode.children.get(nextPrefix);
                    word += nextPrefix;
                }

                return word;
            }
            else{
                return word+findCommonPrefix(prefix,curNode.children.keySet());
            }

        }
        else{
            return null;
        }
    }

    public String getGoodWordStartingWith(String s) {
        LinkedList<String> allCommonPrefix = new LinkedList<>();
        String word = "";
        int index = 0;
        String prefix = s.substring(index,index+1);
        TrieNode curNode = this;
        //Loop through the TrieNode that has the prefix as its key in hashmap
        while(index < s.length()){
            if(curNode.children.containsKey(prefix)){
                curNode = curNode.children.get(prefix);
                word += prefix;
                index++;
                if(index == s.length()){
                    prefix = "";
                    break;
                }

                prefix = Character.toString(s.charAt(index));

            }
            else {
                index++;
                if(index == s.length()){
                    break;
                }
                prefix += Character.toString(s.charAt(index));
            }
        }
        //if prefix is not "", no key in curNode contain current prefix
        //Check each key to see if there is any key that has the common prefix as the current prefix
        if(prefix != ""){
            String commonPrefixKey = findCommonPrefix(prefix, curNode.children.keySet());
            //if there is no prefix in the key that matches with current prefix, return null
            if(commonPrefixKey == null){
                return null;
            }
            else{
                word+= commonPrefixKey;
                curNode = curNode.children.get(commonPrefixKey);
                if(curNode.isWord == true){
                    allCommonPrefix.add(word);
                }
            }
        }

        //get all word start from curNode and store them in a linked list
        getAllWord(curNode, allCommonPrefix, word);
        Iterator<String> allPrefixIterator = allCommonPrefix.iterator();
        while(allPrefixIterator.hasNext()){
            String w = allPrefixIterator.next();
            //if the prefix is odd, find a odd length word, else find a even length word
            if(s.length()%2 == w.length()%2){
                return w;
            }
        }
        //if the linkedlist is empty, return null
        if(allCommonPrefix.isEmpty()){
            return null;
        }
        //else there is no word that matches the requirement, return the first word in the list
        else{
            return allCommonPrefix.getFirst();
        }
    }

    //Find the key that has a prefix matches with the prefix in the parameter
    private String findCommonPrefix(String s, Set<String> set){
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
            String cur = iterator.next();
            int index = 0;
            while(index < s.length()){
                if(s.charAt(index)==cur.charAt(index)){
                    index++;
                }
                else{
                    break;
                }
            }
            if(index == s.length()){
                return cur;
            }
        }
        return null;
    }

    //recurssively store all keys in a linkedlist from a given TrieNode in the parameter
    private void getAllWord(TrieNode node, LinkedList<String> list, String prefix){
        Iterator<String> keySet = node.children.keySet().iterator();
        while(keySet.hasNext()) {
            String key = keySet.next();
            if (node.children.get(key).isWord == true) {
                String newPrefix = prefix + key;
                list.add(newPrefix);
                getAllWord(node.children.get(key), list, newPrefix);
            } else {
                String newPrefix = prefix + key;
                getAllWord(node.children.get(key), list, newPrefix);
            }
        }
    }
}
