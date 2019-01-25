package com.magewr.sample.lib;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.magewr.sample.lib.textswitcher.AdvTextSwitcher;
import com.magewr.sample.lib.textswitcher.Switcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FlightBoardTextManager {

    private static TextSwitcherManagerBuilder builder;
    private ArrayList<ArrayList<AdvTextSwitcher>> switcherList = new ArrayList<>();
    private HashMap<AdvTextSwitcher, Switcher> map = new HashMap<>();

    private ArrayList<LinearLayout> layoutList = new ArrayList<>();
    private OnRowClickListener rowClickListener;

    private int maxRow;
    private int maxColumn;

    private int[] realColumnIndex;

    //For AutoLoop
    private int[] animationCount;
    private boolean autoLoop = false;
    private int autoLoopStartRow = -1;
    private ArrayList<String> stringsForAutoLoop;

    public interface OnRowClickListener {
        void onRowClicked(int rowIndex);
    }

    public static class TextSwitcherManagerBuilder {
        private int row = 0;
        private int column = 0;
        private Context context;
        private ViewGroup parent;
        private int textSize = 16;      // default size 16sp
        private int blockSize = 16;     // default size 16dp
        private int[] blockMargin = {2, 0, 2, 0};   // default block margin
        private int[] textMargin = {0, 8, 0, 8};   // default text(string) margin
        private String[] charArray = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "-", ":", ".", "&"};

        private TextSwitcherManagerBuilder(Context context) {
            this.context = context;
        }

        public TextSwitcherManagerBuilder size(int column, int row) {
            builder.column = column;
            builder.row = row;

            return builder;
        }

        public TextSwitcherManagerBuilder into (ViewGroup viewGroup) {
            builder.parent = viewGroup;

            return builder;
        }

        public TextSwitcherManagerBuilder blockSize (int dp) {
            builder.blockSize = dp;

            return builder;
        }

        public TextSwitcherManagerBuilder textSize (int sp) {
            builder.textSize = sp;

            return builder;
        }

        public TextSwitcherManagerBuilder charList (String[] charList) {
            builder.charArray = charArray;

            return builder;
        }

        public TextSwitcherManagerBuilder blockMargin(int left, int top, int right, int bottom) {
            builder.blockMargin[0] = left;
            builder.blockMargin[1] = top;
            builder.blockMargin[2] = right;
            builder.blockMargin[3] = bottom;

            return builder;
        }

        public TextSwitcherManagerBuilder textMargin(int left, int top, int right, int bottom) {
            builder.textMargin[0] = left;
            builder.textMargin[1] = top;
            builder.textMargin[2] = right;
            builder.textMargin[3] = bottom;

            return builder;
        }

        public FlightBoardTextManager build() {
            FlightBoardTextManager manager = new FlightBoardTextManager();
            manager.maxRow = row;
            manager.maxColumn = column;
            manager.animationCount = new int[row];

            for (int i = 0 ; i < row ; i++ ) {
                LinearLayout layout = new LinearLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertDpToPixel(context, textMargin[0]), convertDpToPixel(context, textMargin[1]), convertDpToPixel(context, textMargin[2]), convertDpToPixel(context, textMargin[3]));
                layout.setLayoutParams(params);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                manager.switcherList.add(i, new ArrayList<>());

                for (int j = 0 ; j < column ; j++ ) {
                    AdvTextSwitcher advTextSwitcher = new AdvTextSwitcher(context, i, j, textSize);
                    advTextSwitcher.setTexts(charArray);
                    Switcher switcher = new Switcher().attach(advTextSwitcher).setDuration(100);

                    LinearLayout.LayoutParams switcherParams = new LinearLayout.LayoutParams(convertDpToPixel(context,blockSize), ViewGroup.LayoutParams.WRAP_CONTENT);
                    switcherParams.setMargins(convertDpToPixel(context, blockMargin[0]), convertDpToPixel(context, blockMargin[1]), convertDpToPixel(context, blockMargin[2]), convertDpToPixel(context, blockMargin[3]));
                    advTextSwitcher.setLayoutParams(switcherParams);

                    layout.addView(advTextSwitcher);

                    manager.switcherList.get(i).add(j, advTextSwitcher);
                    manager.map.put(advTextSwitcher, switcher);
                }
                layout.setOnClickListener(v -> manager.onRowClicked(layout));
                parent.addView(layout);
                manager.layoutList.add(layout);
            }

            manager.initRandomIndex();
            manager.realColumnIndex = new int[row];

            builder = null;

            return manager;
        }
    }

    public static TextSwitcherManagerBuilder with(Context context) {
        builder = new TextSwitcherManagerBuilder(context);
        return builder;
    }

    private void onRowClicked(LinearLayout layout) {
        if (rowClickListener != null) {
            rowClickListener.onRowClicked(layoutList.indexOf(layout));
        }
    }

    public void setOnRowClickListener (OnRowClickListener rowClickListener) {
        this.rowClickListener = rowClickListener;
    }

    /**
     * Methods For Change String
     */
    public void notifyStringChanged(int row, String string) {
        timerHandler.post(createSpinStartRunnable(row, string));
    }

    public void notifyAllStringChangedWithoutEffect(ArrayList<String> stringArray) {
        for (int i = 0 ; i < maxRow ; i ++) {
            String rowText = stringArray.get(i);
            for (int j = 0 ; j < maxColumn ; j ++) {
                if (rowText.length() <= j)
                    break;
                switcherList.get(i).get(j).overrideText(rowText.substring(j, j + 1));
            }
        }
    }

    public void notifyAllStringChangedFromIndex(int startRow, ArrayList<String> stringArray) {
        timerHandler.post(createRowSpinStartRunnable(startRow, stringArray));
    }

    public void notifyAllStringChanged(ArrayList<String> stringArray) {
        timerHandler.post(createRowSpinStartRunnable(0, stringArray));
    }

    /**
     * Methods For Auto Loop
     */
    public void startAutoLoop(ArrayList<String> stringsForAutoLoop, int startRow) {
        this.autoLoop = true;
        this.autoLoopStartRow = startRow;
        this.stringsForAutoLoop = stringsForAutoLoop;

        notifyStringChanged(startRow, stringsForAutoLoop.get(startRow));
    }

    public void stopAutoLoop() {
        this.autoLoop = false;
        this.autoLoopStartRow = -1;
        this.stringsForAutoLoop = null;
    }

    public boolean isAutoLoop() {
        return autoLoop;
    }

    public void clear() {
        for (ArrayList<AdvTextSwitcher> advTextSwitchers : switcherList) {
            for (AdvTextSwitcher advTextSwitcher : advTextSwitchers) {
                advTextSwitcher.overrideText(" ");
            }
        }
    }

    /**
     * Methods For Spin
     */
    private Handler timerHandler = new Handler();
    private Runnable createSpinStartRunnable (int row, String target) {
        return () -> {
            synchronized (this) {
                String targetChar = " ";
                int randomIndex = getRandomIndex(row, realColumnIndex[row]);
                if (target.length() > randomIndex)
                    targetChar = target.substring(randomIndex, randomIndex + 1);

                AdvTextSwitcher advTextSwitcherLocal = switcherList.get(row).get(randomIndex);
//                if (targetChar.equals(" ") && advTextSwitcherLocal.getCurrentText().equals(targetChar)) {
//
//                }
//                else {
                    Switcher switcherLocal = map.get(advTextSwitcherLocal);

                    final String finalTargetChar = targetChar;
                    advTextSwitcherLocal.setCallback(new AdvTextSwitcher.Callback() {

                        @Override
                        public void onTextChanged(String text) {
                            if (text.equalsIgnoreCase(finalTargetChar))
                                switcherLocal.pause();
                        }

                        @Override
                        public void onSpinStarted(int row) {
                            synchronized (this) {
                                animationCount[row] ++;
                            }
                        }

                        @Override
                        public void onSpinStopped(int row) {
                            synchronized (this) {
                                animationCount[row] --;
                                if (autoLoop && animationCount[row] == 0) {
                                    row ++;
                                    if (row == maxRow)
                                        row = autoLoopStartRow;
                                    notifyStringChanged(row, stringsForAutoLoop.get(row));
                                }
                            }
                        }
                    });

                    switcherLocal.start();
//                }

                realColumnIndex[row]++;
                if (realColumnIndex[row] == maxColumn) {
                    realColumnIndex[row] = 0;
                } else {
                    timerHandler.postDelayed(createSpinStartRunnable(row, target), 10);
                }
            }
        };
    }

    private Runnable createRowSpinStartRunnable(int row, ArrayList<String> stringArray) {
        return () -> {
            if (row >= maxRow || row >= stringArray.size())
                return;
            notifyStringChanged(row, stringArray.get(row));
            timerHandler.postDelayed(createRowSpinStartRunnable(row + 1, stringArray), 2000);
        };
    }

    /**
     * Methods For Random Index
     */
    private ArrayList<ArrayList<Integer>> randomIndex = new ArrayList<>();

    private void initRandomIndex() {
        for (int i = 0; i < maxRow ; i++) {
            randomIndex.add(new ArrayList<>());
            for (int j = 0; j < maxColumn; j++) {
                randomIndex.get(i).add(j);
            }
            Collections.shuffle(randomIndex.get(i));
        }
    }
    synchronized private int getRandomIndex(int realRowIndex, int realColumnIndex) {
        return randomIndex.get(realRowIndex).get(realColumnIndex);
    }

    public static int convertDpToPixel(Context context, float dp){
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
