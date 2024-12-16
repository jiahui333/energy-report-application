import { useEffect, useState } from 'react';
import MeterSelector from './components/MeterSelector';
import MeterReport from './components/MeterDataReport';

function App() {
    const [meters, setMeters] = useState<string[]>([]);
    const [selectedMeter, setSelectedMeter] = useState('');
    const [meterError, setMeterError] = useState('');

    useEffect(() => {
        const fetchMeters = () => {
            fetch('http://localhost:8080/api/meters')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Failed to fetch meters: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    setMeters(data);
                })
                .catch(error => {
                    console.error('Fetch Meters Error:', error);
                    setMeterError('Failed to load meter list. Please try again.');
                });
        };

        fetchMeters();

        const intervalId = setInterval(fetchMeters, 30000); // 30000ms = 30s

        return () => clearInterval(intervalId);
    },[]);

    if (meterError) {
        return (
            <div className="flex items-center justify-center h-screen">
                <p className="text-red-500">{meterError}</p>
            </div>
        );
    }

    return (
        <div className="max-w-5xl mx-auto p-6 bg-gray-100 min-h-screen">
            <h1 className="text-3xl font-bold text-center mb-6">Energy Meter Data Report</h1>
            <MeterSelector
                meters={meters}
                selectedMeter={selectedMeter}
                onChange={setSelectedMeter}
            />
            <MeterReport meterId={selectedMeter} />
        </div>
    );
}

export default App;
