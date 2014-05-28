/*
 * Copyright (C) 2014 Trilogis S.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.trilogis.worldwind.tilecreation.swing.utils;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * @author ndorigatti
 * @version $Id: ParseAllFormat.java 1 2014-05-01 15:22:47Z ndorigatti $
 * <p>Decorator for a {@link Format Format} which only accepts values which can be completely parsed
 * by the delegate format. If the value can only be partially parsed, the decorator will refuse to
 * parse the value.</p>
 */
public class ParseAllFormat extends Format {
  /**
     * 
     */
    private static final long serialVersionUID = 6618344168488002410L;
private final Format fDelegate;

  /**
   * Decorate <code>aDelegate</code> to make sure if parser everything or nothing
   *
   * @param aDelegate The delegate format
   */
  public ParseAllFormat( Format aDelegate ) {
    fDelegate = aDelegate;
  }

  @Override
  public StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {
    return fDelegate.format( obj, toAppendTo, pos );
  }

  @Override
  public AttributedCharacterIterator formatToCharacterIterator( Object obj ) {
    return fDelegate.formatToCharacterIterator( obj );
  }

  @Override
  public Object parseObject( String source, ParsePosition pos ) {
    int initialIndex = pos.getIndex();
    Object result = fDelegate.parseObject( source, pos );
    if ( result != null && pos.getIndex() < source.length() ) {
      int errorIndex = pos.getIndex();
      pos.setIndex( initialIndex );
      pos.setErrorIndex( errorIndex );
      return null;
    }
    return result;
  }

  @Override
  public Object parseObject( String source ) throws ParseException {
    //no need to delegate the call, super will call the parseObject( source, pos ) method
    return super.parseObject( source );
  }
}