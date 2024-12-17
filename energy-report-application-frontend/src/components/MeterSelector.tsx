import { ChangeEvent } from 'react';

type MeterSelectorProps = {
    meters: string[];
    selectedMeter: string;
    onChange: (meterId: string) => void;
}

const MeterSelector = ({ meters, selectedMeter, onChange }: MeterSelectorProps) => {
    const handleChange = (e: ChangeEvent<HTMLSelectElement>) => {
        onChange(e.target.value);
    };

    return (
        <div className="mb-4">
            <label htmlFor="meter-select" className="mr-2 font-medium">Select Meter:</label>
            <select
                id="meter-select"
                value={selectedMeter}
                onChange={handleChange}
                disabled={meters.length === 0}
                className="border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
                <option value="" disabled>{meters.length === 0 ? 'No meter-data available' : 'Select a meter'}</option>
                {meters.map((meterId) => (
                    <option key={meterId} value={meterId}>{meterId}</option>
                ))}
            </select>
        </div>
    );
};

export default MeterSelector;
