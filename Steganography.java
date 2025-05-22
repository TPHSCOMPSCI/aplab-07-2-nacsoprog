import java.awt.*;
import java.util.*;

//dude this was hard to understand
public class Steganography {
    public static void clearLow(Pixel px) {
        int r = (px.getRed() / 4) * 4;
        int g = (px.getGreen() / 4) * 4;
        int b = (px.getBlue() / 4) * 4;
        px.setColor(new Color(r, g, b));
    }


    public static Picture testClearLow(Picture img) {
        Picture result = new Picture(img);
        Pixel[][] grid = result.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                clearLow(px);
            }
        }
        return result;
    }


    public static void setLow(Pixel px, Color col) {
        int r = px.getRed();
        int g = px.getGreen();
        int b = px.getBlue();


        int rLow = col.getRed() / 64;
        int gLow = col.getGreen() / 64;
        int bLow = col.getBlue() / 64;


        r = (r / 4) * 4 + rLow;
        g = (g / 4) * 4 + gLow;
        b = (b / 4) * 4 + bLow;


        px.setColor(new Color(r, g, b));
    }


    public static Picture testSetLow(Picture img, Color col) {
        Picture copy = new Picture(img);
        Pixel[][] grid = copy.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                setLow(px, col);
            }
        }
        return copy;
    }


    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] result = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();


        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[0].length; c++) {
                Color col = source[r][c].getColor();
                int rVal = (col.getRed() % 4) * 64;
                int gVal = (col.getGreen() % 4) * 64;
                int bVal = (col.getBlue() % 4) * 64;
                result[r][c].setColor(new Color(rVal, gVal, bVal));
            }
        }
        return copy;
    }


    public static boolean canHide(Picture base, Picture hidden) {
        return base.getWidth() >= hidden.getWidth() && base.getHeight() >= hidden.getHeight();
    }


    public static Picture hidePicture(Picture main, Picture secret, int rStart, int cStart) {
        Picture merged = new Picture(main);
        Pixel[][] mainPixels = merged.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();


        for (int r = 0; r < secretPixels.length; r++) {
            for (int c = 0; c < secretPixels[0].length; c++) {
                int rPos = rStart + r;
                int cPos = cStart + c;
                if (rPos < mainPixels.length && cPos < mainPixels[0].length) {
                    Color secretCol = secretPixels[r][c].getColor();
                    Color mainCol = mainPixels[rPos][cPos].getColor();
                    int rNew = (mainCol.getRed() / 4 * 4) + (secretCol.getRed() / 64);
                    int gNew = (mainCol.getGreen() / 4 * 4) + (secretCol.getGreen() / 64);
                    int bNew = (mainCol.getBlue() / 4 * 4) + (secretCol.getBlue() / 64);
                    mainPixels[rPos][cPos].setColor(new Color(rNew, gNew, bNew));
                }
            }
        }
        return merged;
    }


    public static boolean isSame(Picture one, Picture two) {
        if (one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
            return false;
        }
        Pixel[][] grid1 = one.getPixels2D();
        Pixel[][] grid2 = two.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }


    public static ArrayList<Point> findDifferences(Picture img1, Picture img2) {
        ArrayList<Point> differences = new ArrayList<>();
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return differences;
        }
        Pixel[][] grid1 = img1.getPixels2D();
        Pixel[][] grid2 = img2.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    differences.add(new Point(c, r));
                }
            }
        }
        return differences;
    }


    public static Picture showDifferentArea(Picture img, ArrayList<Point> diffs) {
        Picture highlighted = new Picture(img);
        if (diffs.isEmpty()) {
            return highlighted;
        }


        int minR = Integer.MAX_VALUE, maxR = Integer.MIN_VALUE;
        int minC = Integer.MAX_VALUE, maxC = Integer.MIN_VALUE;


        for (Point pt : diffs) {
            int r = pt.y;
            int c = pt.x;
            minR = Math.min(minR, r);
            maxR = Math.max(maxR, r);
            minC = Math.min(minC, c);
            maxC = Math.max(maxC, c);
        }


        Graphics2D g = highlighted.createGraphics();
        g.setColor(Color.BLUE);
        g.drawRect(minC, minR, maxC - minC, maxR - minR);
        g.dispose();


        return highlighted;
    }


    public static ArrayList<Integer> encodeString(String input) {
        input = input.toUpperCase();
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            String ch = input.substring(i, i + 1);
            if (ch.equals(" ")) {
                result.add(27);
            } else {
                result.add(letters.indexOf(ch) + 1);
            }
        }
        result.add(0);
        return result;
    }


    public static String decodeString(ArrayList<Integer> values) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = "";
        for (int val : values) {
            if (val == 27) {
                output += " ";
            } else {
                output += letters.substring(val - 1, val);
            }
        }
        return output;
    }


    private static int[] getBitPairs(int val) {
        int[] bits = new int[3];
        for (int i = 0; i < 3; i++) {
            bits[i] = val % 4;
            val = val / 4;
        }
        return bits;
    }


    public static void hideText(Picture img, String message) {
        ArrayList<Integer> codes = encodeString(message);
        ArrayList<int[]> bits = new ArrayList<>();
        Pixel[][] grid = img.getPixels2D();
       


        for(int num : codes) {
            int[] bitPair = getBitPairs(num);
            bits.add(bitPair);
        }


        int i = 0;
        int j = 0;
            for (int c = 0; c < codes.size(); c++) {
                int redBit = bits.get(c)[0];
                int greenBit = bits.get(c)[1];
                int blueBit = bits.get(c)[2];


                Pixel px = grid[i][j];
                int rNew = (px.getRed() / 4) * 4 + redBit;
                int gNew = (px.getGreen() / 4) * 4 + greenBit;
                int bNew = (px.getBlue() / 4) * 4 + blueBit;


                px.setColor(new Color(rNew, gNew, bNew));
                j++;
                if(j == grid[0].length) {
                    j = 0;
                    i++;
                }
            }




    }


    public static String revealText(Picture img) {
        ArrayList<Integer> letters = new ArrayList<>();
        Pixel[][] grid = img.getPixels2D();


        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                Color col = px.getColor();
                int r = col.getRed() % 4;
                r +=  col.getGreen() % 4 * 4;
                r += col.getBlue() % 4 * 16;
                if(r == 0){
                    return decodeString(letters);
                }
                letters.add(r);
            }
        }
        return decodeString(letters);
    }


    public static void main(String[] args) {
        Picture beach = new Picture("beach.jpg");
        Picture arch = new Picture("arch.jpg");
        beach.explore();
        Picture pink = testSetLow(beach, Color.PINK);
        pink.explore();
        Picture revealed = revealPicture(pink);
        revealed.explore();


        System.out.println(canHide(beach, arch));
        if (canHide(beach, arch)) {
            Picture merged = hidePicture(beach, arch, 0, 0);
            merged.explore();
            Picture unhidden = revealPicture(merged);
            unhidden.explore();
        }


        Picture swan1 = new Picture("swan.jpg");
        Picture swan2 = new Picture("swan.jpg");
        System.out.println("Are they the same? " + isSame(swan1, swan2));
        swan1 = testClearLow(swan1);
        System.out.println("Cleared low bits: " + isSame(swan1, swan2));


        Picture a1 = new Picture("arch.jpg");
        Picture a2 = new Picture("arch.jpg");
        Picture k = new Picture("koala.jpg");
        Picture r = new Picture("robot.jpg");
        ArrayList<Point> diffs = findDifferences(a1, a2);
        System.out.println("Size: " + diffs.size());
        diffs = findDifferences(a1, k);
        System.out.println("Size: " + diffs.size());
        a2 = hidePicture(a1, r, 65, 102);
        diffs = findDifferences(a1, a2);
        System.out.println("Size after hiding: " + diffs.size());
        a1.show();
        a2.show();


        Picture hall = new Picture("femaleLionAndHall.jpg");
        Picture bot = new Picture("robot.jpg");
        Picture flower = new Picture("flower1.jpg");
        Picture h2 = hidePicture(hall, bot, 50, 300);
        Picture h3 = hidePicture(h2, flower, 115, 275);
        h3.explore();
        if (!isSame(hall, h3)) {
            Picture outline = showDifferentArea(hall, findDifferences(hall, h3));
            outline.show();
            Picture reveal = revealPicture(h3);
            reveal.show();
        }


        Picture msgPic = new Picture("beach.jpg");
        hideText(msgPic, "THIS WAS TOO HARD");
        String hiddenText = revealText(msgPic);
        System.out.println("Secret Message: " + hiddenText);


        Picture bike = new Picture("blueMotorcycle.jpg");
        bike.explore();
        bike.explore();
    }
}