import { useEffect, useState } from 'react';
import { Report } from '../types';

type MeterReportProps = {
    meterId: string;
}

const MeterReport = ({ meterId }: MeterReportProps) => {
    const [report, setReport] = useState<Report | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!meterId) return;

        setLoading(true);
        setError('');
        setReport(null);

        fetch(`http://localhost:8080/api/report?meterId=9346bfb3-20aa-3412-ffab-44f88b917919`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                setReport(data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setError('Failed to load report. Please try again.');
                setLoading(false);
            });
    }, [meterId]);

    if (!meterId) {
        return <div className="text-center text-gray-500">Please select a meter from the dropdown above.</div>;
    }

    if (loading) return <div className="text-center">Loading...</div>;
    if (error) return <div className="text-center text-red-500">{error}</div>;

    if (!report) return null;

    return (
        <div className="bg-white shadow-md rounded-lg p-6 mt-4">
            <p className="mb-4"><span className="font-medium">Meter ID:</span> {report.meterId}</p>
            <div className="mb-4 flex space-x-8">
                <div><span className="font-medium">Total Energy (kWh):</span> {report.totalEnergy}</div>
                <div><span className="font-medium">Total Cost ($):</span> {report.totalCost.toFixed(3)}</div>
            </div>

            <h3 className="text-lg font-semibold mb-2">Hourly Details</h3>
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white">
                    <thead>
                    <tr>
                        <th className="py-2 px-4 border-b">Start Hour (UTC)</th>
                        <th className="py-2 px-4 border-b">Used Energy (kWh)</th>
                        <th className="py-2 px-4 border-b">Cost ($)</th>
                    </tr>
                    </thead>
                    <tbody>
                    {report.hourlyReports.map((hourlyReport, index) => (
                        <tr key={index} className={index % 2 === 0 ? 'bg-gray-50' : 'bg-white'}>
                            <td className="py-2 px-4 border-b text-center">{hourlyReport.hour}</td>
                            <td className="py-2 px-4 border-b text-center">{hourlyReport.kwhUsed}</td>
                            <td className="py-2 px-4 border-b text-center">{hourlyReport.cost.toFixed(3)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MeterReport;
