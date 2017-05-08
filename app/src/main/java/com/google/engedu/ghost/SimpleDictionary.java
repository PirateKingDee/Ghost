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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private ArrayList<String> oddWords;
    private ArrayList<String> evenWords;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        oddWords = new ArrayList<>();
        evenWords = new ArrayList<>();
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH){
                if(word.length()%2==1){
                    oddWords.add(word);
                }
                else{
                    evenWords.add(word);
                }
                words.add(word);
            }

        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        int mid = words.size()/2;
        String word = words.get(mid);
        int first = 0;
        int last = words.size()-1;
        while(first <last&&word.length()>prefix.length()&&(!prefix.equals(word.substring(0, prefix.length())))){
            int index = 0;
            while(prefix.charAt(index) == word.charAt(index)){
                index++;
            }
            if(prefix.charAt(index)<word.charAt(index)){
                last = mid;
                mid = (first + mid)/2;
            }
            else{
                first = mid+1;
                mid = (mid+1 + last)/2;
            }
            word = words.get(mid);
        }
        if(word.length()>prefix.length()&&prefix.equals(word.substring(0, prefix.length()))){
            return word;
        }
        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        int mid = words.size()/2;
        String word = words.get(mid);
        int first = 0;
        int last = words.size()-1;
        while(first <last&&word.length()>=prefix.length()&&(!prefix.equals(word.substring(0, prefix.length())))){
            int index = 0;
            while(prefix.charAt(index) == word.charAt(index)){
                index++;
            }
            if(prefix.charAt(index)<word.charAt(index)){
                last = mid;
                mid = (first + mid)/2;
            }
            else{
                first = mid+1;
                mid = (mid+1 + last)/2;
            }
            word = words.get(mid);
        }
        if(word.length()>=prefix.length()&&prefix.equals(word.substring(0, prefix.length()))){
            return word;
        }
        return null;
    }
}
