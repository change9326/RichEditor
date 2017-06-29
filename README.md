##RichEditor
类似新浪微博EditText，可@某人，#插入话题，表情。

![](http://i.imgur.com/R6O62Pk.gif)

###实现思路
1. 在光标处插入特殊字符

        //将特殊字符插入到EditText 中显示
        int index = getSelectionStart();//光标位置
        Editable editable = getText();//原先内容
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        Spanned htmlText = Html.fromHtml(String.format(String.format("<font color='%s'>" + insertContent + "</font>", insertColor)));
        spannableStringBuilder.insert(index, htmlText);
        spannableStringBuilder.insert(index + htmlText.length(), "\b");
        setText(spannableStringBuilder);
        setSelection(index + htmlText.length() + 1);
2. 实现特殊字符选中删除效果
        
        /**
         * 监听删除键 <br/>
         * 1.光标在话题后面,将整个话题内容删除 <br/>
         * 2.光标在普通文字后面,删除一个字符
         *
         */
        this.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();

                    /**
                     * 如果光标起始和结束不在同一位置,删除文本
                     */
                    if (selectionStart != selectionEnd) {
                        // 查询文本是否属于目标对象,若是移除列表数据
                        String tagetText = getText().toString().substring(
                                selectionStart, selectionEnd);
                        for (int i = 0; i < insertModelList.size(); i++) {
                            InsertModel object = insertModelList.get(i);
                            if (tagetText.equals(object.getInsertContent())) {
                                insertModelList.remove(object);
                            }
                        }
                        return false;
                    }


                    int lastPos = 0;
                    Editable editable = getText();
                    // 遍历判断光标的位置
                    for (int i = 0; i < insertModelList.size(); i++) {
                        String objectText = insertModelList.get(i).getInsertContent();
                        lastPos = getText().toString().indexOf(objectText, lastPos);
                        if (lastPos != -1) {
                            if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + objectText.length())) {
                                // 选中话题
                                setSelection(lastPos, lastPos + objectText.length());
                                // 设置背景色
                                editable.setSpan(new BackgroundColorSpan(BACKGROUND_COLOR), lastPos, lastPos + objectText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return true;
                            }
                        }
                        lastPos += objectText.length();
                    }
                }

                return false;
            }
        });
3. 设置点击特殊字符光标自动移动到特殊字符之后

        /**
         * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
         *
         */
        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (insertModelList == null || insertModelList.size() == 0)
            return;
        int startPostion = 0;
        int endPostion = 0;
        String insertContent = "";
        for (int i = 0; i < insertModelList.size(); i++) {
            insertContent = insertModelList.get(i).getInsertContent();
            startPostion = getText().toString().indexOf(insertContent);
            endPostion = startPostion + insertContent.length();
            if (startPostion != -1 && selStart > startPostion
                    && selStart <= endPostion) {// 若光标处于话题内容中间则移动光标到话题结束位置
                setSelection(endPostion);
            }
        }}