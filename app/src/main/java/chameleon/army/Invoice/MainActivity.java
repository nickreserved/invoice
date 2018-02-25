package chameleon.army.Invoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		setSpinner(R.id.spFPA, R.array.FPA);
		setSpinner(R.id.spFE, R.array.FE);
		setSpinner(R.id.spHolds, holdList);
		((ArrayAdapter) ((Spinner) findViewById(R.id.spHolds)).getAdapter()).add(getString(R.string.hldOther));

		((Spinner) findViewById(R.id.spContractorType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				TextView tv = (TextView) findViewById(R.id.tvContractorInfo);
				String s = getResources().getStringArray(R.array.contractorInfo)[position];
				if (s.equals("")) tv.setVisibility(View.GONE);
				else {
					tv.setText(s);
					tv.setVisibility(View.VISIBLE);
				}
				findViewById(R.id.layFE).setVisibility(position != 0 /* Όχι ιδιώτης */ ? View.GONE : View.VISIBLE);
				findViewById(R.id.layFPA).setVisibility(position == 2 /* Στρατός */ ? View.GONE : View.VISIBLE);
				findViewById(R.id.cbConstruction).setVisibility(position != 0 /* Όχι ιδιώτης */ ? View.GONE : View.VISIBLE);
				calculation();
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		});

        AdapterView.OnItemSelectedListener swListener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) { calculation(); }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        };
        ((Spinner) findViewById(R.id.spAmountType)).setOnItemSelectedListener(swListener);
        ((Spinner) findViewById(R.id.spInvoiceType)).setOnItemSelectedListener(swListener);

        EditText editText = (EditText) findViewById(R.id.txtAmount);
		editText.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { calculation(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        ((Switch) findViewById(R.id.swAuto)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.layAdvanced).setVisibility(View.GONE);
                    findViewById(R.id.layAutomatic).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(R.string.tvAutoOn));
                } else {
                    findViewById(R.id.layAdvanced).setVisibility(View.VISIBLE);
                    findViewById(R.id.layAutomatic).setVisibility(View.GONE);
					((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(R.string.tvAutoOff));
                }
                calculation();
            }
        });

        CompoundButton.OnCheckedChangeListener cbListener = new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { calculation(); }
        };
        ((CheckBox) findViewById(R.id.cbSelf)).setOnCheckedChangeListener(cbListener);
        ((CheckBox) findViewById(R.id.cbConstruction)).setOnCheckedChangeListener(cbListener);
    }

	// Κοινός κώδικας που σετάρει το spinner του ΦΠΑ και ΦΕ
	void setSpinner(int resSpinner, int resArray) {
		setSpinner(resSpinner, getResources().getStringArray(resArray));
	}
	// Κοινός κώδικας που σετάρει το spinner του ΦΠΑ, κρατήσεων και ΦΕ
	<T> void setSpinner(int resSpinner, T[] array) {
		Spinner spinner = (Spinner) findViewById(resSpinner);
		ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
				new ArrayList<>(Arrays.asList(array)));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
		if (id == R.id.action_about) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.about)
					.setMessage(String.format(getString(R.string.aboutInfo), BuildConfig.VERSION_NAME))
					.setPositiveButton(R.string.OK, null)
					.setNeutralButton(R.string.web, new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://sourceforge.net/projects/ha-invoice/files/latest/download")));
						}
					})
					.setCancelable(true)
					.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
		final int other = parentView.getCount() - 1;
		if (other == position) {
			final EditText txtNum = new EditText(this);
			txtNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			txtNum.setHint("%");

			final MainActivity ths = this;
			new AlertDialog.Builder(this)
					.setTitle(R.string.newValue)
					.setView(txtNum)
					.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							try {
								// Μετατροπή της εισόδου σε αριθμό στο [0, 80]
								Double num = Double.valueOf(txtNum.getText().toString());
								if (num > 80) {    // Η είσοδος δεν είναι έγκυρος αριθμός
									parentView.setSelection(0);
									Toast.makeText(ths, R.string.invalidValue, Toast.LENGTH_SHORT).show();
								}
								// Αν ο αριθμός ήδη υπάρχει στη λίστα...
								for (int z = 0; z < other; ++z)
									if (Double.valueOf(parentView.getItemAtPosition(z).toString()).equals(num)) {
										parentView.setSelection(z);    // ...τον επιλέγει
										Toast.makeText(ths, R.string.existedValue, Toast.LENGTH_SHORT).show();
										return;
									}
								// ...ειδάλλως τον εισάγει
                                @SuppressWarnings("unchecked")
								ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) parentView.getAdapter();
                                adapter.insert(num.toString().replaceAll("\\.0$", ""), other);  // Αφαιρεί το αντιαισθητικό ".0" από το "23.0" και το εισάγει στη λίστα
								adapter.notifyDataSetChanged();
								setFEInfo(parentView);
                            } catch (NumberFormatException e) {}
						}
					})
					.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							parentView.setSelection(0); setFEInfo(parentView);
						}
					})
					.show();
		} else setFEInfo(parentView);
	}
	@Override public void onNothingSelected(AdapterView<?> parentView) {}

	void setFEInfo(AdapterView s) {
		if (s == findViewById(R.id.spFE)) {
			String [] ar = getResources().getStringArray(R.array.FEInfo);
			int sel = s.getSelectedItemPosition();
			String txt = sel < ar.length ? ar[sel] : "";
			((TextView) findViewById(R.id.tvFEInfo)).setText(txt);
		}
		calculation();
	}

	Hold getHold(double val) {
		for (Hold item : holdList)
			if (Math.abs(item.total() - val) < 1e-8) return item;
		return null;
	}

    // Εύρεση καθαρής αξίας
    double calculateNet(int contractor, int amountType, double amount, double fpa, double holds, double fe) {
        switch(contractor) {
            case 0:	// Ιδιώτης
                switch(amountType) {
                    case 1: amount /= 1.0 + fpa; break;	// Καταλογιστέο
                    case 2: amount /= 1.0 + fpa - holds; break; // Πληρωτέο
                    case 3: // Υπόλοιπο πληρωτέο
                        // Στις εργολαβίες το ΦΕ υπολογίζεται επί της καθαρής αξίας, ειδάλλως επί της καθαρής αξίας μειον κρατήσεις
                        double feFactor = 1.0 - (fe == 0.03 ? 0 : holds);
                        amount /= 1.0 + fpa - holds - fe * feFactor;
                        // default: Καθαρή αξία
                }
                break;
            case 1:	// Δημόσιο
                switch(amountType) {
                    case 1: amount /= 1.0 + fpa + holds; break;	// Καταλογιστέο
                    case 2: case 3: amount /= 1.0 + fpa; // Πληρωτέο ή Υπόλοιπο πληρωτέο
                        // default: Καθαρή αξία
                }
                break;
            default:    //case 2:	// Στρατός
                if (amountType == 1) amount /= 1.0 + holds;	// Καταλογιστέο
                // else: Καθαρή αξία ή Πληρωτέο ή Υπόλοιπο πληρωτέο
        }
        // Στρογγυλοποίηση
        return Math.round(amount * 100.0) / 100.0;
    }

    // Όλο το πρόγραμμα εδώ!
    void calculation() {
		try {
			// Λήψη όλων των απαραίτητων δεδομένων για τους υπολογισμούς
			int contractor = ((Spinner) findViewById(R.id.spContractorType)).getSelectedItemPosition();
			int amountType = ((Spinner) findViewById(R.id.spAmountType)).getSelectedItemPosition();
            int invoiceType = ((Spinner) findViewById(R.id.spInvoiceType)).getSelectedItemPosition();
            boolean auto = ((CompoundButton) findViewById(R.id.swAuto)).isChecked();
			boolean construction = ((CompoundButton) findViewById(R.id.cbConstruction)).isChecked();
            double amount = Double.parseDouble(((EditText) (findViewById(R.id.txtAmount))).getText().toString());
			double fpaPercent = Double.parseDouble(((Spinner) findViewById(R.id.spFPA)).getSelectedItem().toString()) / 100.0;
			double fePercent, holdsPercent;
			Hold hold = null;
            // Αυτόματη εύρεση κρατήσεων - ΦΕ
            // Το πρόβλημα εδώ είναι ότι δεν ξέρουμε την καθαρή αξία!
            // Για το λόγο αυτό κάποιοι έλεγχοι θα γίνουν μεταγενέστερα.
            if (auto) {
				boolean self = ((CompoundButton) findViewById(R.id.cbSelf)).isChecked();
				// Σε κατασκευαστικές δαπάνες, προμηθευτής είναι πάντα ιδιώτης
				if (contractor != 0) construction = false;	 // Όχι ιδιώτης
                // Υπολογισμός του ΦΕ
                if (contractor != 0) fePercent = 0;	 // Όχι ιδιώτης
                // Μεταγενέστερα: if (net <= 150) fePercent = 0;
                else if (invoiceType == 2) fePercent = 0.01; // Προμήθεια υγρών καυσίμων
                else if (invoiceType == 1) /* Παροχή υπηρεσιών */ fePercent = construction /* Κατασκευή έργου */ ? 0.03 : 0.08;
                else /*if (invoiceType == 0)*/ fePercent = 0.04; // Προμήθεια υλικών
                // Υπολογισμός κρατήσεων
                if (contractor != 0) /* Όχι ιδιώτης */ hold = getHold(holdsPercent = self /* Ιδιοι πόροι */ ? 0.14 : 0.04);
                else {
                    hold = getHold(holdsPercent = self /* Ιδιοι πόροι */ ? 0.14096 : 0.04096);
                    if (calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, fePercent) > 2500)
                        hold = getHold(holdsPercent = self /* Ιδιοι πόροι */ ? 0.141996 : 0.041996);
                }
                // Μεταγενέστερος έλεγχος: if (net <= 150) fePercent = 0;
                if (calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, 0) <= 150) fePercent = 0;
            } else {
				// Εύρεση κρατήσεων και ΦΕ
				Object o = ((Spinner) findViewById(R.id.spHolds)).getSelectedItem();
				if (o instanceof Hold) { hold = (Hold) o; holdsPercent = hold.total(); }
				else holdsPercent = Double.parseDouble(o.toString()) / 100.0;
				fePercent = Double.parseDouble(((Spinner) findViewById(R.id.spFE)).getSelectedItem().toString()) / 100.0;
			}
			// Εύρεση καθαρής αξίας
            amount = calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, fePercent);
			// Υπολογισμοί ενδιάμεσων τιμών
			double fpa = Math.round(amount * fpaPercent * 100.0) / 100.0;
			double holds = Math.round(amount * holdsPercent * 100.0) / 100.0;
			double mixed = amount;
			if (contractor != 2) mixed += fpa;	// όχι Στρατός
			if (contractor != 0) mixed += holds;	// Δημόσιο, Στρατός
			//mixed = Math.round(mixed * 100.0) / 100.0;	// fix truncation errors
			double final1 = mixed - holds;//Math.round((mixed - holds) * 100.0) / 100.0;	// fix truncation errors
			double fe = amount - (fePercent == 0.03 ? 0 : holds);
			fe = Math.round(fe * fePercent * 100.0) / 100.0;
			double final2 = final1 - fe;//Math.round((final1 - fe) * 100.0) / 100.0;	// fix truncation errors
			// Εξαγωγή αποτελεσμάτων
			DecimalFormat df = new DecimalFormat("0.####%");	// ποσοστά
			DecimalFormat df2 = new DecimalFormat("0.00¤");	// νομισματικά
			String txt = String.format(getString(R.string.resNet), df2.format(amount));
			String txtHolds = String.format(getString(R.string.resHolds), df.format(holdsPercent), df2.format(holds));
			if (contractor != 0 /* Δημόσιο, Στρατός */) txt += "+ " + txtHolds;
			if (contractor != 2 /* όχι Στρατός */) txt += String.format(getString(R.string.resVAT), df.format(fpaPercent), df2.format(fpa));
			txt += String.format(getString(R.string.resMixed), df2.format(mixed), txtHolds, df2.format(final1));
			if (contractor == 0 /* Ιδιώτης */ && fePercent > 0)
				txt += String.format(getString(R.string.resFE), df.format(fePercent), df2.format(fe), df2.format(final2));
			((TextView) findViewById(R.id.tvResults)).setText(txt);
			// Εξαγωγή απαιτούμενων
			txt = "";
            if (contractor == 0) { // Ιδιώτης
                if (mixed > 1500) txt += getString(R.string.reqTaxCurrency) + "\n";
                if (mixed > 3000) txt += getString(R.string.reqInsuranceCurrency) + "\n";
            }
            if (contractor != 2 /* όχι Στρατός */ && (amount > 2500 || construction && auto)) txt += getString(R.string.reqContract) + "\n";
            if (amount > 60000) txt += getString(R.string.reqCompetitionFormal) + "\n";
            else if (amount > 15000 || construction && auto) txt += getString(R.string.reqCompetitionInformal) + "\n";
			if (auto && construction && invoiceType == 1 /* Παροχή υπηρεσιών */)
				txt += String.format(getString(R.string.reqConstructionContractor),
						df2.format(amount * 0.01), df2.format(amount * 0.002), df2.format(amount * 0.005), df2.format(amount * 0.006));
			if (txt.equals("")) txt = getString(R.string.reqEmpty);
			((TextView) findViewById(R.id.tvRequirements)).setText(txt);
			// Ανάλυση κρατήσεων
			if (hold != null) {
				double[] holdsAll = hold.euro(holds);
				String[] txtHoldsAll = getResources().getStringArray(R.array.hldParts);
				String str = getString(R.string.hldPart);
				txt = String.format(str, getString(R.string.hldTotal), df.format(holdsPercent), df2.format(holds));
				for (int z = 0; z < holdsAll.length; ++z)
					if (z >= hold.data.length) break;
					else if (hold.data[z] != 0)
						txt += String.format(str, txtHoldsAll[z], df.format(hold.data[z]), df2.format(holdsAll[z]));
			} else txt = getString(R.string.hldFail);
			((TextView) findViewById(R.id.tvHoldAnalysis)).setText(txt);
			// Εμφάνιση των widgets που είναι κρυφά όσο δεν υπάρχει αποτέλεσμα
            findViewById(R.id.layOut).setVisibility(View.VISIBLE);
		} catch (NumberFormatException e) {
			findViewById(R.id.layOut).setVisibility(View.GONE);
		}
	}

	//                             ΜΤΣ,   Χαρτόσημο, ΟΓΑ,    ΕΑΑΔΗΣΥ, ΒΑΜ, ΕΚΟΕΜΣ, ΓΓΚΠ
	final private Hold[] holdList = {
			new Hold(new double[] {0.04,    0.0008,  0.00016}),									// 4.096
			new Hold(new double[] {0.04,    0.00083, 0.000166, 0.001}),							// 4.1996
			new Hold(new double[] {0.03904, 0.0008,  0.00016}),									// 4
			new Hold(new double[] {0.04,    0.00085, 0.00017,  0.001, 0,    0,    0.001}),		// 4.302
			new Hold(new double[] {0,       0,       0,        0,     0.02, 0.08}),				// 10
			new Hold(new double[] {0.03904, 0.0008,  0.00016,  0,     0.02, 0.08}),				// 14
			new Hold(new double[] {0.04,    0.0008,  0.00016,  0,     0.02, 0.08}),				// 14.096
			new Hold(new double[] {0.04,    0.00083, 0.000166, 0.001, 0.02, 0.08}),				// 14.1996
	};

	public class Hold {
		public Hold(double[] holds) { data = holds; }
		public double total() { return sum(data); }
		@Override public String toString() { return Double.toString(Math.round(total() * 10000000.0)/ 100000.0).replaceAll("\\.0$", ""); }
		public double[] euro(double holds) {
			double euroTotal = 0, euroData[] = new double[data.length];
			double total = total();
			class Pair {
				Pair(double remainder, int id) { this.remainder = remainder; this.id = id; }
				double remainder;
				int id;
			}
			ArrayList<Pair> remainders = new ArrayList<>();
			for (int z = 0; z < data.length; ++z) {
				double t = holds * data[z] / total;
				remainders.add(new Pair(Math.round(t) - t, z));
				euroTotal += euroData[z] = Math.round(t * 100.0) / 100.0;
			}

			// Σφάλματα στρογγυλοποίησης
			int remainder = (int) Math.round((euroTotal - holds) * 100.0);
			if (remainder != 0) {
				Collections.sort(remainders, new Comparator<Pair>() {
					@Override public int compare(Pair a, Pair b) {
						if (a.remainder < b.remainder) return -1;
						if (a.remainder > b.remainder) return 1;
						return 0;
					}
				});
				if (remainder > 0)
					for (int z = 0; z < remainder; ++z) {
						Pair p = remainders.get(remainders.size() - 1 - z);
						euroData[p.id] -= 0.01;
					}
				else
					for (int z = 0; z < -remainder; ++z) {
						Pair p = remainders.get(z);
						euroData[p.id] += 0.01;
					}
			}
			return euroData;
		}
		private double sum(double[] array) {
			double sum = 0;
			for (double item : array) sum += item;
			return sum;
		}
		final private double[] data;
	}
}
