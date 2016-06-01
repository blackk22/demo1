
package com.wonhigh.base.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wonhigh.base.R;

/** 
* @ClassName: CommonMsgDialog 
* @Description: 操作警告提示框(包含title，提示信息，确定及取消按钮)
* 用于敏感操作时提示:如删除重要信息
* @author li.xy
* @date 2014-6-17 下午5:47:10  
*/
public class CommonMsgDialog extends Dialog implements OnClickListener{
        /** 
        * @ClassName: OnSubmitListener 
        * @Description: 按钮的回调接口
        * @author li.xy
        * @date 2014-6-18 上午10:11:30  
        */
        public interface OnSubmitListener{
               //确定
               public void ok(String address);
        }
        private TextView titleTextView;
        private EditText msgEditetxt;
        private Button cancelButton,okButton;
        protected OnSubmitListener onSubmitListener = null;
        protected Context context;
        private String title;
        private String msg;
        
        public CommonMsgDialog(Context context,String title,String msg) {
                super(context/*,R.style.commonDialog*/);
                this.context = context;
                this.title = title;
                this.msg = msg;
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) { 
                super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.dialog_common_msg);
                titleTextView = (TextView) findViewById(R.id.titleBar);
                msgEditetxt = (EditText) findViewById(R.id.et_dialog_msg);
                cancelButton = (Button) findViewById(R.id.cancelBtn);
                okButton = (Button) findViewById(R.id.okBtn);
                titleTextView.setText(title);
                
                msgEditetxt.setText(msg);
                msgEditetxt.setSelection(msg.length()); //设置光标位置
                msgEditetxt.setSelected(false);
                
                cancelButton.setOnClickListener(this);
                okButton.setOnClickListener(this);
        }
        
		public OnSubmitListener getOnSubmitListener() {
			return onSubmitListener;
		}

		public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
			this.onSubmitListener = onSubmitListener;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.okBtn) {
				if (onSubmitListener != null) {
					onSubmitListener.ok(msgEditetxt.getText().toString().trim());
				}
				dismiss();
			} else if (v.getId() == R.id.cancelBtn) {
				dismiss();
			}			
		}
		
		@Override
		public void show() {
			if (context instanceof Activity && !((Activity) context).isFinishing()) {
				super.show();
			}		
		}
}
