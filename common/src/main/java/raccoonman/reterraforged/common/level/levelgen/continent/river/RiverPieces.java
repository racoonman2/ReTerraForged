/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.level.levelgen.continent.river;

import java.util.ArrayList;
import java.util.List;

public class RiverPieces {
    public static final RiverPieces NONE = new RiverPieces();

    private List<RiverNode> rivers;
    private List<RiverNode> lakes;

    public RiverPieces() {
    	this.rivers = new ArrayList<>();
    	this.lakes = new ArrayList<>();
    }
    
    public RiverPieces reset() {
    	this.rivers.clear();
    	this.lakes.clear();
    	return this;
    }

    public int riverCount() {
        return this.rivers.size();
    }

    public int lakeCount() {
        return this.lakes.size();
    }

    public RiverNode river(int i) {
        return this.rivers.get(i);
    }

    public RiverNode lake(int i) {
        return this.lakes.get(i);
    }

    public void addRiver(RiverNode node) {
    	this.rivers.add(node);
    }

    public void addLake(RiverNode node) {
    	this.lakes.add(node);
    }
}
