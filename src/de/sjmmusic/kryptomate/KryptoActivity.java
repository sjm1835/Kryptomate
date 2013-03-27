/*******************************************************************************
Copyright (c) 2013, Sebastian J. Mielke
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
  * The names of its contributors may not be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/

package de.sjmmusic.kryptomate;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

public class KryptoActivity extends Activity implements InputFilter, TextWatcher {

	ToggleButton tgMethod, tgMode;
	SeekBar sbShift;
	EditText edInput, edOutput, edKey;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tgMode = (ToggleButton) findViewById(R.id.tgMode);
		tgMethod = (ToggleButton) findViewById(R.id.tgMethod);
		sbShift = (SeekBar) findViewById(R.id.sbShift);
		edInput = (EditText) findViewById(R.id.edInput);
		edOutput = (EditText) findViewById(R.id.edOutput);
		edKey = (EditText) findViewById(R.id.edKey);
		
		sbShift.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					refreshResult();
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});

		tgMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edKey.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				sbShift.setVisibility(isChecked ? View.GONE : View.VISIBLE);
				refreshResult();
			}
		});

		tgMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				refreshResult();
			}
		});

		edInput.addTextChangedListener(this);
		edKey.addTextChangedListener(this);
		edKey.setFilters(new InputFilter[]{this});
		tgMethod.setChecked(true);
		tgMethod.toggle();
		tgMode.toggle();
	}

	@Override
	public CharSequence filter(CharSequence source, int start,
			int end, Spanned dest, int dstart, int dend) {
		for (int i = start; i < end; i++) {
			if (!Character.isLetter(source.charAt(i)))
				return "";
			else if (Character.isLowerCase(source.charAt(i)))
				return "" + Character.toUpperCase(source.charAt(i));
		}
		return null;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		refreshResult();
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}
	
	@Override
	public void afterTextChanged(Editable s) {}
	
	public void refreshResult() {
		if(!tgMethod.isChecked()) //Caesar
		{
			tgMode.setTextOn("Encrypt (" + String.valueOf(sbShift.getProgress()) + ")");
			tgMode.setTextOff("Decrypt (" + String.valueOf(sbShift.getProgress()) + ")");
			tgMode.setText((tgMode.isChecked() ? "Encrypt (" : "Decrypt(")
					+ String.valueOf(sbShift.getProgress()) + ")");
			edOutput.setText(caesar(edInput.getText().toString(),
					sbShift.getProgress() * (tgMode.isChecked() ? 1 : -1)));
		} else { //Vigenere
			tgMode.setTextOn("Encrypt");
			tgMode.setTextOff("Decrypt");
			tgMode.setText(tgMode.isChecked() ? "Encrypt" : "Decrypt");
			edOutput.setText(vigenere(edInput.getText().toString(),
					edKey.getText().toString(), (tgMode.isChecked() ? 1 : -1)));
		}
	}
	

	public static String caesar(String input, int shift) {
		String output = "";
		int a;
		for (int i = 0; i < input.length(); i++){
			a = input.charAt(i);
			if(a < 65 || a > 122 || (a > 90 && a < 97)) //Non-Alphabetical
				output += (char) a;
			else if (a >= 65 && a <= 90) { //Uppercase
				a += shift;
				if (a < 65) a += 26;
				if (a > 90) a -= 26;
				output += (char) a;
			}
			else { //Lowercase
				a += shift;
				if (a < 97) a += 26;
				if (a > 122) a -= 26;
				output += (char) a;
			}
		}
		return output;
	}

	public static String vigenere (String input, String key, int crypt) {
		if (key.length() == 0) return "";
		String output = "";
		int a, k;
		for (int i = 0; i < input.length(); i++){
			a = input.charAt(i);
			k = crypt * (key.charAt(i % key.length()) - 65);
			if(a < 65 || a > 122 || (a > 90 && a < 97)) //Non-Alphabetical
				output += (char) a;
			else if (a >= 65 && a <= 90) { //Uppercase
				a += k;
				if (a < 65) a += 26;
				if (a > 90) a -= 26;
				output += (char) a;
			}
			else { //Lowercase
				a += k;
				if (a < 97) a += 26;
				if (a > 122) a -= 26;
				output += (char) a;
			}
		}
		return output;
	}
}