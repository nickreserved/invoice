package chameleon.army.Invoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public final static int DEFAULT_FPA = 3;	//23
    public final static int DEFAULT_HOLDS = 3;	//4.1996
    public final static int DEFAULT_FE = 3;		//4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		setSpinner(R.id.spFPA, R.array.FPA, DEFAULT_FPA);
		setSpinner(R.id.spHolds, R.array.Holds, DEFAULT_HOLDS);
		setSpinner(R.id.spFE, R.array.FE, DEFAULT_FE);

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
                View fpa = findViewById(R.id.layFPA), fe = findViewById(R.id.layFE);
                fe.setVisibility(position > 1 ? View.GONE : View.VISIBLE);
                fpa.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
                calculation();
            }
            @Override  public void onNothingSelected(AdapterView<?> parentView) {}
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
                } else {
                    findViewById(R.id.layAdvanced).setVisibility(View.VISIBLE);
                    findViewById(R.id.layAutomatic).setVisibility(View.GONE);
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

	// Κοινός κώδικας που σετάρει το spinner του ΦΠΑ, κρατήσεων και ΦΕ
	void setSpinner(int resSpinner, int resArray, int def) {
		Spinner spinner = (Spinner) findViewById(resSpinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
				new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(resArray))));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(def);
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
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(R.string.aboutInfo);
            dlgAlert.setTitle(R.string.about);
            dlgAlert.setPositiveButton(R.string.OK, null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
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
									parentView.setSelection(DEFAULT_FPA);    // Κανονικά πρέπει να πάει στην προηγούμενη τιμή.
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
                            parentView.setSelection(DEFAULT_FPA);    // Κανονικά πρέπει να πάει στην προηγούμενη τιμή.
                            setFEInfo(parentView);
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

    // Εύρεση καθαρής αξίας
    double calculateNet(int contractor, int amountType, double amount, double fpa, double holds, double fe) {
        switch(contractor) {
            case 0:	// Ιδιώτης
            case 1:	// Ιδιώτης που δε θέλει να πληρώσει κρατήσεις (παράνομο)
                switch(amountType) {
                    case 1: amount /= 1.0 + fpa; break;	// Καταλογιστέο
                    case 2: amount /= 1.0 + fpa - holds; break; // Πληρωτέο
                    case 3: // Υπόλοιπο πληρωτέο
                        // Στις εργολαβίες το ΦΕ υπολογίζεται επί της καθαρής αξίας, ειδάλλως επί της καθαρής αξίας μειον κρατήσεις
                        double feFactor = 1.0 - (fe == 0.03 ? 0 : holds);
                        amount /= 1.0 + fpa - holds - fe * feFactor;
                        // default: Καθαρή αξία
                }
                if (contractor == 1)	// Ιδιώτης που δε θέλει να πληρώσει κρατήσεις (παράνομο)
                    amount /= 1.0 - holds / (1.0 + fpa);	// Καθαρή αξία
                break;
            case 2:	// Δημόσιο
                switch(amountType) {
                    case 1: amount /= 1.0 + fpa + holds; break;	// Καταλογιστέο
                    case 2: case 3: amount /= 1.0 + fpa; // Πληρωτέο ή Υπόλοιπο πληρωτέο
                        // default: Καθαρή αξία
                }
                break;
            case 3:	// Στρατός
                // Καταλογιστέο
                if (amountType == 1) amount /= 1.0 + holds;
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
            boolean self = ((CompoundButton) findViewById(R.id.cbSelf)).isChecked();
            boolean construction = ((CompoundButton) findViewById(R.id.cbConstruction)).isChecked();
            double amount = Double.parseDouble(((EditText) (findViewById(R.id.txtAmount))).getText().toString());
			double fpaPercent = Double.parseDouble(((Spinner) findViewById(R.id.spFPA)).getSelectedItem().toString()) / 100.0;
			double fePercent = Double.parseDouble(((Spinner) findViewById(R.id.spFE)).getSelectedItem().toString()) / 100.0;
			double holdsPercent = Double.parseDouble(((Spinner) findViewById(R.id.spHolds)).getSelectedItem().toString()) / 100.0;
            // Αυτόματη εύρεση κρατήσεων - ΦΕ
            // Το πρόβλημα εδώ είναι ότι δεν ξέρουμε την καθαρή αξία!
            // Για το λόγο αυτό κάποιοι έλεγχοι θα γίνουν μεταγενέστερα.
            if (auto) {
                // Υπολογισμός του ΦΕ
                if (contractor > 1) fePercent = 0; // Δημόσιο, Στρατός
                // Μεταγενέστερα: if (net <= 150) fePercent = 0;
                else if (invoiceType == 2) fePercent = 0.01; // Προμήθεια υγρών καυσίμων
                else if (invoiceType == 1) /* Παροχή υπηρεσιών */ fePercent = construction /* Κατασκευή έργου */ ? 0.03 : 0.08;
                else if (invoiceType == 0) fePercent = 0.04; // Προμήθεια υλικών
                // Υπολογισμός κρατήσεων
                if (contractor == 3) /* Στρατός */ holdsPercent = self /* Ιδιοι πόροι */ ? 0.1 : 0.04;
                else if (contractor < 2 /* Ιδιώτης */ && invoiceType == 1 /* Παροχή υπηρεσιών */ && construction /* Κατασκευή έργου */) {
                    holdsPercent = self /* Ιδιοι πόροι */ ? 0.1512 : 0.0512;
                    if (calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, fePercent) > 2500)
                        holdsPercent = self /* Ιδιοι πόροι */ ? 0.152236 : 0.052236;
                } else {
                    holdsPercent = self /* Ιδιοι πόροι */ ? 0.14096 : 0.04096;
                    if (calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, fePercent) > 2500)
                        holdsPercent = self /* Ιδιοι πόροι */ ? 0.141996 : 0.041996;
                }
                // Μεταγενέστερος έλεγχος: if (net <= 150) fePercent = 0;
                if (calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, 0) <= 150) fePercent = 0;
            }
			// Εύρεση καθαρής αξίας
            amount = calculateNet(contractor, amountType, amount, fpaPercent, holdsPercent, fePercent);
			// Υπολογισμοί ενδιάμεσων τιμών
			double fpa = Math.round(amount * fpaPercent * 100.0) / 100.0;
			double holds = Math.round(amount * holdsPercent * 100.0) / 100.0;
			double mixed = amount;
			if (contractor != 3) mixed += fpa;	// όχι Στρατός
			if (contractor > 1) mixed += holds;	// Δημόσιο, Στρατός
			mixed = Math.round(mixed * 100.0) / 100.0;	// fix truncation errors
			double final1 = Math.round((mixed - holds) * 100.0) / 100.0;	// fix truncation errors
			double fe = amount - (fePercent == 0.03 ? 0 : holds);
			fe = Math.round(fe * fePercent * 100.0) / 100.0;
			double final2 = Math.round((final1 - fe) * 100.0) / 100.0;	// fix truncation errors
			// Εξαγωγή αποτελεσμάτων
			DecimalFormat df = new DecimalFormat("0.####%");	// ποσοστά
			DecimalFormat df2 = new DecimalFormat("0.00¤");	// νομισματικά
			String txtHolds = getResources().getString(R.string.resHolds) + " " + df.format(holdsPercent) + ":\t" + df2.format(holds) + "\n";
			String txt = getResources().getString(R.string.resNet) + ":\t" + df2.format(amount) + "\n";
			if (contractor > 1 /* Δημόσιο, Στρατός */) txt += "+ " + txtHolds;
			if (contractor != 3 /* όχι Στρατός */) txt += getResources().getString(R.string.resVAT) + " " + df.format(fpaPercent) + ":\t" + df2.format(fpa) + "\n";
			txt += getResources().getString(R.string.resMixed) + ":\t" + df2.format(mixed) + "\n" +
					"‒ " + txtHolds +
					getResources().getString(R.string.resFinal) + ":\t" + df2.format(final1);
			if (contractor < 2 /* Ιδιώτης */ && fePercent > 0)
				txt += "\n" + getResources().getString(R.string.resFE) + " " + df.format(fePercent) + ":\t" + df2.format(fe) + "\n" +
					getResources().getString(R.string.resFinal2) + ":\t" + df2.format(final2);
            TextView tvResults = (TextView) findViewById(R.id.tvResults);
			tvResults.setText(txt);
			// Εξαγωγή απαιτούμενων
			txt = "";
            if (contractor < 2) { // Ιδιώτης
                if (mixed > 1500) txt += getResources().getString(R.string.reqTaxCurrency) + "\n";
                if (mixed > 3000) txt += getResources().getString(R.string.reqInsuranceCurrency) + "\n";
            }
            if (contractor != 3 /* όχι Στρατός */ && (amount > 2500 || construction && auto)) txt += getResources().getString(R.string.reqContract) + "\n";
            if (amount > 60000) txt += getResources().getString(R.string.reqCompetitionFormal) + "\n";
            else if (amount > 15000 || construction && auto) txt += getResources().getString(R.string.reqCompetitionInformal) + "\n";
            TextView tvRequirements = (TextView) findViewById(R.id.tvRequirements);
            if (txt.equals("")) txt = "‒";
            tvRequirements.setText(txt);
			// Εμφάνιση των widgets που είναι κρυφά όσο δεν υπάρχει αποτέλεσμα
            findViewById(R.id.layOut).setVisibility(View.VISIBLE);
		} catch (NumberFormatException e) {
			findViewById(R.id.layOut).setVisibility(View.GONE);
		}
	}
}
