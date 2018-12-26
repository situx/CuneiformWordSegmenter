/*
 *
 *  *
 *  *  * This file is part of CuneiformWordSegmenter.
 *  *  *
 *  *  *   CuneiformWordSegmenter is free software: you can redistribute it and/or modify
 *  *  *     it under the terms of the GNU General Public License as published by
 *  *  *     the Free Software Foundation, either version 3 of the License, or
 *  *  *     (at your option) any later version.
 *  *  *
 *  *  *    CuneiformWordSegmenter is distributed in the hope that it will be useful,
 *  *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  *     GNU General Public License for more details.
 *  *  *
 *  *  *     You should have received a copy of the GNU General Public License
 *  *  *     along with CuneiformWordSegmenter.  If not, see <http://www.gnu.org/licenses/>.
 *  *  *
 *  *
 *
 */

package de.unifrankfurt.cs.acoli.akkad.main.gui.util;

/*
 * [The "BSD license"]
 * Copyright (c) 2011, abego Software GmbH, Germany (http://www.abego.org)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the abego Software GmbH nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

        import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
        import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
        import org.abego.treelayout.TreeForTreeLayout;
        import org.abego.treelayout.util.DefaultTreeForTreeLayout;
        import java.util.Map;

/**
 * Creates "Sample" trees, e.g. to be used in demonstrations.
 *
 * @author Udo Borkowski (ub@abego.org)
 */
public class SampleTreeFactory {

    /**
     * Returns a "Sample" tree with {@link POSInBox} items as nodes.
     */
    /*public static TreeForTreeLayout<POSInBox> createSampleTree(Map<Integer,Tuple<String,POSDefinition>> posdefs) {

        POSInBox root = new POSInBox("root", 40, 20);
        DefaultTreeForTreeLayout<POSInBox> tree = new DefaultTreeForTreeLayout<POSInBox>(
                root);
        for(Integer wordOrd:posdefs.keySet()){
            tree.addChild(root,new POSInBox(posdefs.get(wordOrd).getOne()+"("+posdefs.get(wordOrd).getTwo().getPosTag().toString()+")",80,36));
        }
        return tree;
    }

    /**
     * Returns a "Sample" tree with {@link POSInBox} items as nodes.
     */
    /*public static TreeForTreeLayout<POSInBox> createSampleTree2() {
        POSInBox root = new POSInBox("prog", 40, 20);
        POSInBox n1 = new POSInBox("classDef", 65, 20);
        POSInBox n1_1 = new POSInBox("class", 50, 20);
        POSInBox n1_2 = new POSInBox("T", 20, 20);
        POSInBox n1_3 = new POSInBox("{", 20, 20);
        POSInBox n1_4 = new POSInBox("member", 60, 20);
        POSInBox n1_5 = new POSInBox("member", 60, 20);
        POSInBox n1_5_1 = new POSInBox("<ERROR:int>", 90, 20);
        POSInBox n1_6 = new POSInBox("member", 60, 20);
        POSInBox n1_6_1 = new POSInBox("int", 30, 20);
        POSInBox n1_6_2 = new POSInBox("i", 20, 20);
        POSInBox n1_6_3 = new POSInBox(";", 20, 20);
        POSInBox n1_7 = new POSInBox("}", 20, 20);


        DefaultTreeForTreeLayout<POSInBox> tree = new DefaultTreeForTreeLayout<POSInBox>(
                root);
        tree.addChild(root, n1);
        tree.addChild(n1, n1_1);
        tree.addChild(n1, n1_2);
        tree.addChild(n1, n1_3);
        tree.addChild(n1, n1_4);
        tree.addChild(n1, n1_5);
        tree.addChild(n1_5, n1_5_1);
        tree.addChild(n1, n1_6);
        tree.addChild(n1_6,n1_6_1);
        tree.addChild(n1_6,n1_6_2);
        tree.addChild(n1_6,n1_6_3);
        tree.addChild(n1, n1_7);
        return tree;
    } */
}