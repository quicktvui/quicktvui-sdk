/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Kohei TAKETA <k-tak@void.in> (Java port)
 *   Kazutoshi Satoda
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package com.quicktvui.support.subtitle.converter.universalchardet.prober.statemachine;


import com.quicktvui.support.subtitle.converter.universalchardet.Constants;

public class ISO2022JPSMModel extends SMModel {
    ////////////////////////////////////////////////////////////////
    // constants
    ////////////////////////////////////////////////////////////////
    public static final int ISO2022JP_CLASS_FACTOR = 10;
    
    
    ////////////////////////////////////////////////////////////////
    // methods
    ////////////////////////////////////////////////////////////////
	public ISO2022JPSMModel() {
        super(
                new PkgInt(PkgInt.INDEX_SHIFT_4BITS, PkgInt.SHIFT_MASK_4BITS, PkgInt.BIT_SHIFT_4BITS, PkgInt.UNIT_MASK_4BITS, iso2022jpClassTable),
                ISO2022JP_CLASS_FACTOR,
                new PkgInt(PkgInt.INDEX_SHIFT_4BITS, PkgInt.SHIFT_MASK_4BITS, PkgInt.BIT_SHIFT_4BITS, PkgInt.UNIT_MASK_4BITS, iso2022jpStateTable),
                iso2022jpCharLenTable,
                Constants.CHARSET_ISO_2022_JP
                );
    }
    

    ////////////////////////////////////////////////////////////////
    // constants continued
    ////////////////////////////////////////////////////////////////
    private static int[] iso2022jpClassTable = new int[] {
        PkgInt.pack4bits(2,0,0,0,0,0,0,0),  // 00 - 07 
        PkgInt.pack4bits(0,0,0,0,0,0,2,2),  // 08 - 0f 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 10 - 17 
        PkgInt.pack4bits(0,0,0,1,0,0,0,0),  // 18 - 1f 
        PkgInt.pack4bits(0,0,0,0,7,0,0,0),  // 20 - 27 
        PkgInt.pack4bits(3,0,0,0,0,0,0,0),  // 28 - 2f 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 30 - 37 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 38 - 3f 
        PkgInt.pack4bits(6,0,4,0,8,0,0,0),  // 40 - 47 
        PkgInt.pack4bits(0,9,5,0,0,0,0,0),  // 48 - 4f 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 50 - 57 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 58 - 5f 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 60 - 67 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 68 - 6f 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 70 - 77 
        PkgInt.pack4bits(0,0,0,0,0,0,0,0),  // 78 - 7f 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // 80 - 87 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // 88 - 8f 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // 90 - 97 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // 98 - 9f 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // a0 - a7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // a8 - af 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // b0 - b7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // b8 - bf 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // c0 - c7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // c8 - cf 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // d0 - d7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // d8 - df 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // e0 - e7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // e8 - ef 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2),  // f0 - f7 
        PkgInt.pack4bits(2,2,2,2,2,2,2,2)   // f8 - ff 
    };
    
    private static int[] iso2022jpStateTable = new int[] {
        PkgInt.pack4bits(START,    3,ERROR,START,START,START,START,START),//00-07 
        PkgInt.pack4bits(START,START,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR),//08-0f 
        PkgInt.pack4bits(ERROR,ERROR,ERROR,ERROR,ITSME,ITSME,ITSME,ITSME),//10-17 
        PkgInt.pack4bits(ITSME,ITSME,ITSME,ITSME,ITSME,ITSME,ERROR,ERROR),//18-1f 
        PkgInt.pack4bits(ERROR,    5,ERROR,ERROR,ERROR,    4,ERROR,ERROR),//20-27 
        PkgInt.pack4bits(ERROR,ERROR,ERROR,    6,ITSME,ERROR,ITSME,ERROR),//28-2f 
        PkgInt.pack4bits(ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ITSME,ITSME),//30-37 
        PkgInt.pack4bits(ERROR,ERROR,ERROR,ITSME,ERROR,ERROR,ERROR,ERROR),//38-3f 
        PkgInt.pack4bits(ERROR,ERROR,ERROR,ERROR,ITSME,ERROR,START,START) //40-47 
    };
    
    private static int[] iso2022jpCharLenTable = new int[] {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
}
